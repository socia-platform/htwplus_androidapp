package htw_berlin.de.htwplus.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hamnaberg.json.Collection;

import org.apache.http.impl.cookie.BasicClientCookie;

import htw_berlin.de.htwplus.ApplicationController;
import htw_berlin.de.htwplus.R;
import htw_berlin.de.htwplus.VolleyNetworkController;
import htw_berlin.de.htwplus.datamodel.ApiError;
import htw_berlin.de.htwplus.util.JsonCollectionHelper;


public class MainActivity extends Activity implements Response.Listener, Response.ErrorListener, View.OnClickListener {
    public static final String REQUEST_TAG = "MainVolleyActivity";
    private TextView mTextView;
    private Button mButton;
    private Button mButtonPost;
    private EditText mEditText;
    private Dialog mAuthDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.mTextView);
        mButton = (Button) findViewById(R.id.mButton);
        mButtonPost = (Button) findViewById(R.id.mButtonPost);
        mButton.setOnClickListener(this);
        mButtonPost.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.mEditText);

        mAuthDialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mAuthDialog.setContentView(R.layout.oauth2_dialog);

        if (!ApplicationController.getSharedPrefController().hasAccessToken())
            makeAuthentification();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
/*        if(v == mButton) {
            ApplicationController.getVolleyController().getUser(1, REQUEST_TAG, this, this);
        } else if(v == mButtonPost) {
            try {
                String postMessage = mEditText.getText().toString();
                if (!postMessage.isEmpty())
                    ApplicationController.getVolleyController().addPost(postMessage, 1, 1, null, null, REQUEST_TAG, this, this);
                else
                    Toast.makeText(getApplicationContext(), "Bitte Post-Message eingeben!", Toast.LENGTH_LONG).show();
            } catch (JSONException jex) {
                Toast.makeText(getApplicationContext(), "JSON parse Exception!\nSiehe konsole!", Toast.LENGTH_LONG).show();
                jex.printStackTrace();
            }
        }*/

        Intent intent = new Intent(this, PostListViewActivity.class);
        MainActivity.this.startActivity(intent);

/*        Intent intent = new Intent(this, UserListViewActivity.class);
        MainActivity.this.startActivity(intent);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyNetworkController.getInstance().cancelRequest(REQUEST_TAG);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mTextView.setText("Error\n");
        if (error != null) {
            String errorMessage = error.getMessage() != null ? error.getMessage() : "";
            mTextView.setText(mTextView.getText() + errorMessage);
        }
    }

    @Override
    public void onResponse(Object response) {
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
    }

    private void makeAuthentification() {
        WebView webView = (WebView)mAuthDialog.findViewById(R.id.webv);
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

                if (url.contains("authorizationCode=") && !authComplete) {
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("authorizationCode");
                    ApplicationController.getSharedPrefController().setAuthorizationToken(authCode);


                    makeAccessTokenRequest();
                    Toast.makeText(getApplicationContext(), "Authorization Code is: " + authCode, Toast.LENGTH_SHORT).show();
                    authComplete = true;
                    ApplicationController.getSharedPrefController().removeAuthorizationToken();
                    mAuthDialog.dismiss();
                } else if (url.contains("clientId=")) {
                    BasicClientCookie sessionCookie = extractSessionCookie(CookieManager.getInstance().getCookie(url));
                    ApplicationController.getSharedPrefController().setPlaySessionValue(sessionCookie.getValue());
                } else if (url.contains("error=access_denied")) {
                    authComplete = true;
                    //setResult(Activity.RESULT_CANCELED, resultIntent);
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                    mAuthDialog.dismiss();
                }
            }
        });
        mAuthDialog.show();
        mAuthDialog.setCancelable(false);
    }

    public void makeAccessTokenRequest() {
        syncCookie("PLAY_SESSION",
                ApplicationController.getSharedPrefController().getApiUrl().getHost(),
                ApplicationController.getSharedPrefController().getPlaySessionValue());
        String authToken = ApplicationController.getSharedPrefController().getAuthorizationToken();
        ApplicationController.getVolleyController().getAccessToken(authToken, "AccessTokenRequest", this, this);
    }

    private void syncCookie(String name, String domain, String value) {
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        String cookieString = name + "=\"" + value + "; domain=" + domain + "\"";
        cookieManager.setCookie(domain, cookieString);
        CookieSyncManager.getInstance().sync();
    }

    private BasicClientCookie extractSessionCookie(String rawCookiesString) {
        BasicClientCookie sessionCookie = null;
        String[] rawCookies = rawCookiesString.split(";");
        String rawSessionCookie = null;
        for (String rawCookie : rawCookies) {
            if (rawCookie.contains("PLAY_SESSION=")) {
                rawSessionCookie = rawCookie;
                break;
            }
        }
        String[] rawCookieNameAndValue = rawSessionCookie.split("\"");
        if (rawCookieNameAndValue.length == 2) {
            rawCookieNameAndValue[0] = rawCookieNameAndValue[0].substring(0, rawCookieNameAndValue[0].indexOf("="));
            String cookieName = rawCookieNameAndValue[0].trim();
            String cookieValue = rawCookieNameAndValue[1].trim();
            sessionCookie = new BasicClientCookie(cookieName, cookieValue);
        }
        return sessionCookie;
    }
}
