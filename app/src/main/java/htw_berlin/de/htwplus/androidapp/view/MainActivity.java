package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hamnaberg.json.Collection;

import java.net.URL;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.VolleyNetworkController;
import htw_berlin.de.htwplus.androidapp.datamodel.ApiError;
import htw_berlin.de.htwplus.androidapp.util.JsonCollectionHelper;


public class MainActivity extends Activity implements Response.Listener, Response.ErrorListener, View.OnClickListener {
    public static final String REQUEST_TAG = "MainVolleyActivity";
    private TextView mTextView;
    private Dialog mAuthDialog;
    private Button postsBtn;
    private Button groupsBtn;
    private Button friendsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        postsBtn = (Button) findViewById(R.id.PostBtn);
        groupsBtn = (Button) findViewById(R.id.GroupsBtn);
        friendsBtn = (Button) findViewById(R.id.FriendsBtn);

        postsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostListViewActivity.class);
                startActivity(intent);
            }
        });

        /*
        groupsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Grou.class);
                startActivity(intent);
            }
        });*/

        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserListViewActivity.class);
                startActivity(intent);
            }
        });

        mAuthDialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mAuthDialog.setContentView(R.layout.oauth2_dialog);

        ApplicationController.getSharedPrefController().removeAccessToken();
        ApplicationController.getSharedPrefController().removeApiUrl();
        if (!ApplicationController.getSharedPrefController().hasAccessToken()) {
            if(ApplicationController.getSharedPrefController().getApiUrl() == null)
                showApiBasicSettingsAlertDialog();
            //makeAuthentification();
        }
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

/*            Intent intent = new Intent(this, PostListViewActivity.class);
        MainActivity.this.startActivity(intent);*/

        Intent intent = new Intent(this, UserListViewActivity.class);
        MainActivity.this.startActivity(intent);
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
                    authComplete = true;
                    ApplicationController.getSharedPrefController().removeAuthorizationToken();
                    mAuthDialog.dismiss();
                }
            }
        });
        mAuthDialog.show();
        mAuthDialog.setCancelable(false);
    }

    public void makeAccessTokenRequest() {
        String authToken = ApplicationController.getSharedPrefController().getAuthorizationToken();
        ApplicationController.getVolleyController().getAccessToken(authToken, "AccessTokenRequest", this, this);
    }

    private void showApiBasicSettingsAlertDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.basic_settings_alert_dialog, null);
        final EditText apiUrlEditText = (EditText) view.findViewById(R.id.apiUrlEditText);
        final AlertDialog builder = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("OK", null) //Button Set to null. We override the onclick
                .create();
        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            URL apiUrl = new URL(apiUrlEditText.getText().toString());
                            Runtime runtime = Runtime.getRuntime();
                            Process proc = runtime.exec("ping -c 1 " + apiUrl.getHost());
                            int mPingResult = proc.waitFor();
                            if(mPingResult == 0) {
                                ApplicationController.getSharedPrefController().setApiUrl(apiUrl);
                                String message = "\"" + apiUrl.toString() + "\" gespeichert";
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                builder.cancel();
                            } else
                                Toast.makeText(getApplicationContext(), "Der Host ist nicht erreichbar", Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), "Diese URL ist ungültig", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        builder.show();
    }
}
