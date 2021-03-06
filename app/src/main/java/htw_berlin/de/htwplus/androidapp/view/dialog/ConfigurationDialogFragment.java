package htw_berlin.de.htwplus.androidapp.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import htw_berlin.de.htwplus.androidapp.Application;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.SharedPreferencesProxy;

/**
 * Represents the configuration dialog view.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class ConfigurationDialogFragment extends DialogFragment
        implements Response.Listener, Response.ErrorListener {

    /**
     * Listener Interface of the configuration dialog to allow callbacks outside.
     */
    public interface ConfigurationDialogListener {
        public void onConfigurationDialogDismissed();
    }

    /** Request tag, which indicates a http post-request to get a initial access token. */
    public static final String VOLLEY_NEW_ACCESS_TOKEN_REQUEST_TAG = "VolleyAccessToken";

    /** Request tag, which indicates a http post-request to get a refreshed access token. */
    public static final String VOLLEY_REFRESH_ACCESS_TOKEN_REQUEST_TAG = "VolleyRefreshAccessToken";

    /** Listener to delivery a callback method. */
    private ConfigurationDialogListener mListener;

    /** View of the dialog. */
    private View mDialogView;

    /** Api url label text view of the view. */
    private TextView mApiUrlLabelTextView;

    /** Api url edit field of the view. */
    private EditText mApiUrlEditText;

    /** Fetch api url button of the view. */
    private Button mFetchApiUrlButton;

    /** Authentication dialog. */
    private Dialog mAuthDialog;

    /** Access token state info text view of the view. */
    private TextView mAccessTokenInfoTextView;

    /** Access token detailed state info text view of the view. */
    private TextView mAccessTokenInfoDetailsTextView;

    /** Open authentication webview button of the view. */
    private Button mOpenAuthViewButton;

    /** Reset login data button of the view. */
    private Button mResetAccessTokenButton;

    /**
     * Called if the dialog attached on the host activity.
     *
     * @param activity Host activity of the dialog.
     *
     * @throws ClassCastException Throws if host activity implements no configuration dialog
     * listener interface.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ConfigurationDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ConfigurationDialogListener");
        }

        mAuthDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mAuthDialog.setContentView(R.layout.oauth2_dialog);
    }

    /**
     * Called if the dialog is starting.
     */
    @Override
    public void onStart() {
        super.onStart();
        fillStateInformations();
    }

    /**
     * Called if the dialog is resuming.
     */
    @Override
    public void onResume() {
        super.onStart();
        fillStateInformations();
    }

    /**
     * Called if the dialog is stopping.
     */
    @Override
    public void onStop() {
        super.onStop();
        Application.network().cancelRequest(VOLLEY_NEW_ACCESS_TOKEN_REQUEST_TAG);
        Application.network().cancelRequest(VOLLEY_REFRESH_ACCESS_TOKEN_REQUEST_TAG);
        mListener.onConfigurationDialogDismissed();
    }

    /**
     * Called if the view for dialog is creating.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     *
     * @return Created view for dialog.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDialogView = inflater.inflate(R.layout.configuration_dialog, container);
        getDialog().setTitle(R.string.configuration_title);
        mApiUrlLabelTextView = (TextView) mDialogView.findViewById(R.id.apiUrlLabelTextView);
        mApiUrlEditText = (EditText) mDialogView.findViewById(R.id.apiUrlEditText);
        mFetchApiUrlButton = (Button) mDialogView.findViewById(R.id.fetchApiUrlButton);
        mAccessTokenInfoTextView =
                (TextView) mDialogView.findViewById(R.id.accessTokenInfoTextView);
        mAccessTokenInfoDetailsTextView =
                (TextView) mDialogView.findViewById(R.id.accessTokenInfoDetailsTextView);
        mOpenAuthViewButton = (Button) mDialogView.findViewById(R.id.openAuthViewButton);
        mResetAccessTokenButton = (Button) mDialogView.findViewById(R.id.resetAccessTokenButton);
        initiateButtonClickListeners();
        return mDialogView;
    }

    /**
     * Called if a http response is received, which is not ok.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        String errorMessage = getText(R.string.error_unexpected_response).toString();
        if (error != null) {
            if ((error.getCause() != null) && (error.getCause().getMessage() != null)) {
                errorMessage += "\n" + error.getCause().getMessage();
            } else {
                if (error.getMessage() != null)
                    errorMessage += "\n" + error.getMessage();
                else
                    errorMessage += "\n" + error.toString();
            }
        }
        error.printStackTrace();
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Called if a http response is received, which is ok.
     *
     * @param response
     */
    @Override
    public void onResponse(Object response) {
        try {
            boolean isAccessTokenExists =
                    Application.preferences().oAuth2().hasAccessToken();
            onVolleyNewAccessTokenResponse(new JSONObject((String) response));
            fillStateInformations();
            if (isAccessTokenExists)
                Toast.makeText(getActivity(),
                        R.string.info_access_token_refreshed,
                        Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),
                    R.string.error_unexpected_response,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called if the response of a http request contains a new access token.
     *
     * @param jsonResponse Body of http response as JSON data
     *
     * @throws JSONException Throws if the given json data is invalid.
     */
    private void onVolleyNewAccessTokenResponse(JSONObject jsonResponse) throws JSONException {
            String accessToken = jsonResponse.getString("access_token");
            String refreshToken = jsonResponse.getString("refresh_token");
            int expiredSeconds = jsonResponse.getInt("expires_in");
            long currentUserId = jsonResponse.getLong("user_id");
            if (!accessToken.isEmpty()) {
                if (!refreshToken.isEmpty())
                    Application.preferences().oAuth2().setRefreshToken(refreshToken);
                if (expiredSeconds > 0) {
                    long expiredMilliSec = System.currentTimeMillis() + (1000 * expiredSeconds);
                    Application.preferences().oAuth2().setExpiredTimeAccessToken
                            (new Date(expiredMilliSec));
                }
                if (currentUserId > 0)
                    Application.preferences().oAuth2().setCurrentUserId(currentUserId);
                Application.preferences().oAuth2().setAccessToken(accessToken);
            }
    }

    /**
     * Initiates listeners for all buttons of the view.
     */
    private void initiateButtonClickListeners() {
        mFetchApiUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFetchApiUrlButtonClicked();
            }
        });

        mOpenAuthViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenAuthViewButtonClicked();
            }
        });

        mResetAccessTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetAccessTokenButton();
            }
        });
    }

    /**
     * Called if was clicked on the fetch api url button.
     */
    private void onFetchApiUrlButtonClicked() {
        try {
            URL apiUrl = new URL(mApiUrlEditText.getText().toString());
            RetrieveHostReachabilityTask reachabilityTask = new RetrieveHostReachabilityTask();
            reachabilityTask.execute(new URL[]{apiUrl});
            mFetchApiUrlButton.setEnabled(false);
        } catch (MalformedURLException e) {
            Toast.makeText(getActivity(),
                    R.string.error_api_url_invalid,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called if was clicked on the open authentication button.
     */
    private void onOpenAuthViewButtonClicked() {
        SharedPreferencesProxy shCon = Application.preferences();
        if (shCon.apiRoute().hasApiUrl()) {
            if (shCon.oAuth2().hasAccessToken() && shCon.oAuth2().hasRefreshToken())
                makeRefreshAccessTokenRequest();
            else
                openAuthentificationDialog();
        } else
            Toast.makeText(getActivity(),
                    R.string.warning_no_api_url,
                    Toast.LENGTH_LONG).show();
    }

    /**
     * Called if was clicked on the reset login data button.
     */
    private void onResetAccessTokenButton() {
        SharedPreferencesProxy shCon = Application.preferences();
        if (shCon.oAuth2().hasAccessToken())
            shCon.oAuth2().removeAccessToken();
        if (shCon.oAuth2().hasRefreshToken())
            shCon.oAuth2().removeRefreshToken();
        if (shCon.oAuth2().hasExpiredTimeAccessToken())
            shCon.oAuth2().removeExpiredTimeAccessToken();
        if (shCon.oAuth2().hasAuthorizationToken())
            shCon.oAuth2().removeAuthorizationToken();
        if (shCon.oAuth2().hasCurrentUserId())
            shCon.oAuth2().removeCurrentUserId();
        fillStateInformations();
    }

    /**
     * Fills all ui components of the view with content depending on the current state.
     */
    private void fillStateInformations() {
        SharedPreferencesProxy shCon = Application.preferences();
        if (shCon.apiRoute().hasApiUrl()) {
            mApiUrlLabelTextView.setText(getText(R.string.configuration_info_api_url_positive));
            mApiUrlEditText.setText(shCon.apiRoute().getApiUrl().toString());
        } else {
            mApiUrlLabelTextView.setText(getText(R.string.configuration_info_api_url_negative));
            mApiUrlEditText.setText("");
        }
        if (shCon.oAuth2().hasAccessToken()) {
            if (shCon.oAuth2().isAccessTokenExpired())
                mAccessTokenInfoTextView.setText(getText(R.string.configuration_info_access_token_negative_expired));
            else
                mAccessTokenInfoTextView.setText(getText(R.string.configuration_info_access_token_positive));
            String details = shCon.oAuth2().getAccessToken();
            if (shCon.oAuth2().hasExpiredTimeAccessToken()) {
                details += "\n" + getText(R.string.access_token_expired_in) + ": ";
                details += new SimpleDateFormat("dd.MM.yyyy HH:mm").format(
                        shCon.oAuth2().getExpiredTimeAccessToken());
            }
            mAccessTokenInfoDetailsTextView.setText(details);
            mOpenAuthViewButton.setText(getText(R.string.configuration_open_auth_dialog_button_refresh));
            mResetAccessTokenButton.setEnabled(true);
        } else {
            mAccessTokenInfoTextView.setText(getText(R.string.configuration_info_access_token_negative));
            mAccessTokenInfoDetailsTextView.setText(
                    getText(R.string.configuration_info_details_access_token));
            mOpenAuthViewButton.setText(getText(R.string.configuration_open_auth_dialog_button));
            mResetAccessTokenButton.setEnabled(false);
        }
    }

    /**
     * Activates and displays a webview in which must the user log in and allows the app access
     * to the HTWPlus API.
     */
    private void openAuthentificationDialog() {
        final WebView webView = (WebView)mAuthDialog.findViewById(R.id.webv);
        webView.getSettings().setJavaScriptEnabled(true);
        Map<String, String> params = new HashMap<>();
        params.put("client_secret", Application.preferences().oAuth2().getClientSecret());
        params.put("redirect_uri", Application.preferences().oAuth2().getAuthCallBackURI());
        params.put("response_type", "code");
        params.put("client_id", Application.preferences().oAuth2().getClientId());
        webView.loadUrl(Application.preferences().apiRoute().authorize(params));
        webView.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String redirectUrl =
                        Application.preferences().oAuth2().getAuthCallBackURI() + "?code=";
                if (url.contains(redirectUrl) && !authComplete) {
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");
                    Application.preferences().oAuth2().setAuthorizationToken(authCode);
                    makeAccessTokenRequest();
                    authComplete = true;
                    Application.preferences().oAuth2().removeAuthorizationToken();
                    fillStateInformations();
                    mAuthDialog.dismiss();
                }
            }
        });
        mAuthDialog.show();
    }

    /**
     * Sends a http post-request to get a access token.
     */
    private void makeAccessTokenRequest() {
        Application.network().getAccessToken(
                VOLLEY_NEW_ACCESS_TOKEN_REQUEST_TAG, this, this);
    }

    /**
     * Sends a http post-request to get a refreshed access token.
     */
    private void makeRefreshAccessTokenRequest() {
        Application.network().refreshAccessToken(
                VOLLEY_REFRESH_ACCESS_TOKEN_REQUEST_TAG, this, this);
    }

    /**
     * Represents a asynchronous task in which tries to send a simple get-request to the entered
     * api url so that checks the reachability.<br /><br />
     *
     * Depending on the reachability of url it displays a toast message and fill state
     * information in the ui components. If the url reachable so the entered api url will be add
     * in api routes preferences.
     */
    public class RetrieveHostReachabilityTask extends AsyncTask<URL, Void, Boolean> {

        @Override
        protected Boolean doInBackground(URL... urls) {
            boolean isReachable = false;
            try {
                URL url = urls[0];
                SocketAddress sockaddr = new InetSocketAddress(url.getHost(), url.getPort());
                Socket sock = new Socket();
                int timeoutMs = 5000;
                sock.connect(sockaddr, timeoutMs);
                isReachable = true;
            } catch (Exception e) {
                isReachable = false;
            }
            return isReachable;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    Application.preferences().apiRoute().setApiUrl(
                            new URL(mApiUrlEditText.getText().toString()));
                    fillStateInformations();
                    Toast.makeText(getActivity(),
                            R.string.api_url_saved,
                            Toast.LENGTH_LONG).show();
                } catch (MalformedURLException muex) {
                    Log.d("ConfigurationDialog", "Exception Occured: ", muex);
                }
            }
            else
                Toast.makeText(getActivity(), R.string.error_host_unreachable,
                        Toast.LENGTH_LONG).show();
            mFetchApiUrlButton.setEnabled(true);
        }
    }
}
