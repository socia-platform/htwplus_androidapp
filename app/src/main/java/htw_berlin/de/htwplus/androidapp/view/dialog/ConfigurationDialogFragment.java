package htw_berlin.de.htwplus.androidapp.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.SharedPreferencesController;
import htw_berlin.de.htwplus.androidapp.VolleyNetworkController;

import org.json.JSONObject;

/**
 * Created by tino on 22.09.15.
 */
public class ConfigurationDialogFragment extends DialogFragment
        implements Response.Listener, Response.ErrorListener {

    public interface ConfigurationDialogListener {
        public void onConfigurationDialogDismissed();
    }

    public static final String VOLLEY_ACCESS_TOKEN_REQUEST_TAG = "VolleyAccessToken";
    private ConfigurationDialogListener mListener;
    private View mDialogView;
    private TextView mApiUrlLabelTextView;
    private EditText mApiUrlEditText;
    private Button mFetchApiUrlButton;
    private Dialog mAuthDialog;
    private TextView mAccessTokenInfoTextView;
    private TextView mAccessTokenInfoDetailsTextView;
    private Button mOpenAuthViewButton;

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

    @Override
    public void onStart() {
        super.onStart();
        fillStateInformations();
    }

    @Override
    public void onResume() {
        super.onStart();
        fillStateInformations();
    }

    @Override
    public void onStop() {
        super.onStop();
        VolleyNetworkController.getInstance().cancelRequest(VOLLEY_ACCESS_TOKEN_REQUEST_TAG);
        mListener.onConfigurationDialogDismissed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDialogView = inflater.inflate(R.layout.configuration_dialog, container);
        getDialog().setTitle(R.string.configuration_title);

        mApiUrlLabelTextView = (TextView) mDialogView.findViewById(R.id.apiUrlLabelTextView);
        mApiUrlEditText = (EditText) mDialogView.findViewById(R.id.apiUrlEditText);
        mFetchApiUrlButton = (Button) mDialogView.findViewById(R.id.fetchApiUrlButton);
        mAccessTokenInfoTextView = (TextView) mDialogView.findViewById(R.id.accessTokenInfoTextView);
        mAccessTokenInfoDetailsTextView = (TextView) mDialogView.findViewById(R.id.accessTokenInfoDetailsTextView);
        mOpenAuthViewButton = (Button) mDialogView.findViewById(R.id.openAuthViewButton);
        initiateButtonClickListeners();

        return mDialogView;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        String errorMessage = getText(R.string.error_unexpected_response).toString();
        if (error != null) {
            if (error.getMessage() != null)
                errorMessage += "\n" + error.getMessage();
        }
        error.printStackTrace();
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Object response) {
        try {
            JSONObject jsonResponse = new JSONObject((String)response);
            String accessToken = jsonResponse.getString("access_token");
            String refreshToken = jsonResponse.getString("refresh_token");
            int expiredSeconds = jsonResponse.getInt("expires_in");
            if (!accessToken.isEmpty()) {
                if (!refreshToken.isEmpty())
                    ApplicationController.getSharedPrefController().setRefreshToken(refreshToken);
                if (expiredSeconds > 0)
                    ApplicationController.getSharedPrefController().setExpiredTimeAccessToken(expiredSeconds);
                ApplicationController.getSharedPrefController().setAccessToken(accessToken);
                fillStateInformations();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),
                    R.string.error_unexpected_response,
                    Toast.LENGTH_LONG).show();
        }
    }

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
    }

    private void onFetchApiUrlButtonClicked() {
        try {
            fetchApiUrl();
            fillStateInformations();
            Toast.makeText(getActivity(),
                    R.string.api_url_saved,
                    Toast.LENGTH_LONG).show();
        } catch (MalformedURLException e) {
            Toast.makeText(getActivity(),
                    R.string.error_api_url_invalid,
                    Toast.LENGTH_LONG).show();
        } catch (SocketTimeoutException e) {
            Toast.makeText(getActivity(),
                    R.string.error_host_unreachable,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void onOpenAuthViewButtonClicked() {
        SharedPreferencesController shCon = ApplicationController.getSharedPrefController();
        if (shCon.hasApiUrl()) {
            if (isHostReachable(shCon.getApiUrl())) {
                openAuthentificationDialog();
                fillStateInformations();
            }
            else
                Toast.makeText(getActivity(),
                        R.string.error_host_unreachable,
                        Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getActivity(),
                    R.string.warning_no_api_url,
                    Toast.LENGTH_LONG).show();
    }

    private void fillStateInformations() {
        SharedPreferencesController shCon = ApplicationController.getSharedPrefController();
        if (shCon.hasApiUrl()) {
            mApiUrlLabelTextView.setText(getText(R.string.configuration_info_api_url_positive));
            mApiUrlEditText.setText(shCon.getApiUrl().toString());
        } else {
            mApiUrlLabelTextView.setText(getText(R.string.configuration_info_api_url_negative));
            mApiUrlEditText.setText("");
        }
        if (shCon.hasAccessToken()) {
            mAccessTokenInfoTextView.setText(getText(R.string.configuration_info_access_token_positive));
            String details = shCon.getAccessToken();
            if (shCon.hasExpiredTimeAccessToken()) {
                details += "\n" + getText(R.string.access_token_expired_in) + ": ";
                details += new SimpleDateFormat("dd.MM.yyyy HH:mm").format(shCon
                        .getExpiredTimeAccessToken());
            }
            mAccessTokenInfoDetailsTextView.setText(details);
            mOpenAuthViewButton.setText(getText(R.string.configuration_open_auth_dialog_button_refresh));
        } else {
            mAccessTokenInfoTextView.setText(getText(R.string.configuration_info_access_token_negative));
            mAccessTokenInfoDetailsTextView.setText(getText(R.string.configuration_info_details_access_token));
            mOpenAuthViewButton.setText(getText(R.string.configuration_open_auth_dialog_button));
        }
    }

    private void fetchApiUrl() throws MalformedURLException, SocketTimeoutException {
        URL apiUrl = new URL(mApiUrlEditText.getText().toString());
        if (isHostReachable(apiUrl))
            ApplicationController.getSharedPrefController().setApiUrl(apiUrl);
        else
            throw new SocketTimeoutException();
    }

    private boolean isHostReachable(URL hostUrl) {
        boolean isReachable = true;
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec("ping -c 1 " + hostUrl.getHost());
            int mPingResult = proc.waitFor();
            if (mPingResult != 0)
                isReachable = false;
        } catch (Exception ex) {
            isReachable = false;
        }
        return isReachable;
    }

    private void openAuthentificationDialog() {
        final WebView webView = (WebView)mAuthDialog.findViewById(R.id.webv);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(ApplicationController.getSharedPrefController().getAuthorizationUrl().toString());
        webView.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String redirectUrl = ApplicationController.getSharedPrefController().getAuthCallBackURI() + "?code=";
                if (url.contains(redirectUrl) && !authComplete) {
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");
                    ApplicationController.getSharedPrefController().setAuthorizationToken(authCode);
                    makeAccessTokenRequest();
                    authComplete = true;
                    ApplicationController.getSharedPrefController().removeAuthorizationToken();
                    fillStateInformations();
                    mAuthDialog.dismiss();
                }
            }
        });
        mAuthDialog.show();
    }

    private void makeAccessTokenRequest() {
        String authToken = ApplicationController.getSharedPrefController().getAuthorizationToken();
        ApplicationController.getVolleyController().getAccessToken(
                authToken,
                VOLLEY_ACCESS_TOKEN_REQUEST_TAG,
                this,
                this);
    }
}
