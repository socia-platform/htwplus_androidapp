package htw_berlin.de.htwplus.androidapp.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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

import net.hamnaberg.json.Collection;

import java.net.MalformedURLException;
import java.net.URL;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.SharedPreferencesController;
import htw_berlin.de.htwplus.androidapp.datamodel.ApiError;
import htw_berlin.de.htwplus.androidapp.util.JsonCollectionHelper;

/**
 * Created by tino on 22.09.15.
 */
public class ConfigurationDialogFragment extends DialogFragment
        implements View.OnClickListener, Response.Listener, Response.ErrorListener {

    public interface ConfigurationDialogListener {
        public void onConfigurationDialogPositiveClick(DialogFragment dialog, URL apiUrl);
    }

    private ConfigurationDialogListener mListener;
    private View mDialogView;
    private TextView mApiUrlLabelTextView;
    private EditText mApiUrlEditText;
    private Button mConfirmButton;
    private TextView mAccessTokenInfoTextView;
    private TextView mAccessTokenInfoDetailsTextView;
    private Button mOpenAuthViewButton;
    private Dialog mAuthDialog;

    // Override the Fragment.onAttach() method to instantiate the ConfigurationDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ConfigurationDialogListener so we can send events to the host
            mListener = (ConfigurationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDialogView = inflater.inflate(R.layout.configuration_dialog, container);
        getDialog().setTitle(R.string.configuration_title);

        mApiUrlLabelTextView = (TextView) mDialogView.findViewById(R.id.apiUrlLabelTextView);
        mApiUrlEditText = (EditText) mDialogView.findViewById(R.id.apiUrlEditText);
        mConfirmButton = (Button) mDialogView.findViewById(R.id.confirmButton);
        mAccessTokenInfoTextView = (TextView) mDialogView.findViewById(R.id.accessTokenInfoTextView);
        mAccessTokenInfoDetailsTextView = (TextView) mDialogView.findViewById(R.id.accessTokenInfoDetailsTextView);
        mOpenAuthViewButton = (Button) mDialogView.findViewById(R.id.openAuthViewButton);

        mConfirmButton.setOnClickListener(this);
        mOpenAuthViewButton.setOnClickListener(this);

        return mDialogView;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mConfirmButton)) {
            try {
                URL apiUrl = new URL(mApiUrlEditText.getText().toString());
                if (isHostReachable(apiUrl))
                    mListener.onConfigurationDialogPositiveClick(ConfigurationDialogFragment.this, apiUrl);
                else
                    Toast.makeText(getActivity(), R.string.error_host_unreachable, Toast.LENGTH_LONG).show();
            } catch (MalformedURLException mex) {
                Toast.makeText(getActivity(), R.string.error_api_url_invalid, Toast.LENGTH_LONG).show();
            }
        } else if (view.equals(mOpenAuthViewButton)) {
            openAuthentificationDialog();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //mTextView.setText("Error\n");
        if (error != null) {
            String errorMessage = error.getMessage() != null ? error.getMessage() : "";
            //mTextView.setText(mTextView.getText() + errorMessage);
        }
    }

    @Override
    public void onResponse(Object response) {
        /*
        try {
            Collection collection = JsonCollectionHelper.parse(response.toString());
            if (!JsonCollectionHelper.hasError(collection)) {
                String accessToken = JsonCollectionHelper.extractAccessToken(collection);
                if (!accessToken.isEmpty())
                    ApplicationController.getSharedPrefController().setAccessToken(accessToken);
            } else {
                ApiError apiError = JsonCollectionHelper.toError(collection);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        */
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
            mAccessTokenInfoDetailsTextView.setText("");
            mOpenAuthViewButton.setText(getText(R.string.configuration_open_auth_dialog_button_refresh));
        } else {
            mAccessTokenInfoTextView.setText(getText(R.string.configuration_info_access_token_negative));
            mAccessTokenInfoDetailsTextView.setText(getText(R.string.configuration_info_details_access_token));
            mOpenAuthViewButton.setText(getText(R.string.configuration_open_auth_dialog_button));
        }
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

    public void makeAccessTokenRequest() {
        String authToken = ApplicationController.getSharedPrefController().getAuthorizationToken();
        ApplicationController.getVolleyController().getAccessToken(authToken, "AccessTokenRequest", this, this);
    }
}
