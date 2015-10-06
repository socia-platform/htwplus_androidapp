package htw_berlin.de.htwplus.androidapp.view;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import htw_berlin.de.htwplus.androidapp.Application;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.SharedPreferencesController;
import htw_berlin.de.htwplus.androidapp.datamodel.User;
import htw_berlin.de.htwplus.androidapp.view.dialog.ConfigurationDialogFragment;

public class MainActivity extends FragmentActivity implements Response.Listener,
        Response.ErrorListener, ConfigurationDialogFragment.ConfigurationDialogListener {
    public static final String REQUEST_TAG = "MainActivity";
    private Button mConfigButton;
    private Button mPostsButton;
    private Button mContactsButton;
    private TextView mMainTextView;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentUser = null;
        mConfigButton = (Button) findViewById(R.id.configurationButton);
        mPostsButton = (Button) findViewById(R.id.postButton);
        mContactsButton = (Button) findViewById(R.id.contactsButton);
        mMainTextView = (TextView) findViewById(R.id.mainTextView);
        initiateButtonClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateState();
    }

    @Override
    public  void onConfigurationDialogDismissed() {
        updateState();
    }

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
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Object response) {
        if (((List<User>)response).size() == 1) {
            currentUser = ((List<User>)response).get(0);
            fillStateInformations();
        }
    }

    private void initiateButtonClickListeners() {

        mPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostsButtonClicked();
            }
        });

        mContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactsButtonClicked();
            }
        });

        mConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfigurationButtonClick();
            }
        });
    }

    private void onPostsButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), PostListViewActivity.class);
        startActivity(intent);
    }

    private void onContactsButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), UserListViewActivity.class);
        startActivity(intent);
    }

    private void onConfigurationButtonClick() {
        showConfigurationDialog();
    }

    private void fillStateInformations() {
        if (!Application.getInstance().isWorkingState()) {
            String warningText = buildWarningText(Application.preferences());
            mMainTextView.setText(warningText);
        } else if (currentUser != null) {
            String welcomeText = getText(R.string.main_welcome).toString();
            welcomeText += ' ' + currentUser.getFirstName() + ' ' + currentUser.getLastName();
            mMainTextView.setText(welcomeText);
        } else
            mMainTextView.setText("");
    }

    private void updateState() {
        fillStateInformations();
        if (Application.getInstance().isWorkingState()) {
            Application.getVolleyController().getUser(
                    Application.preferences().oAuth2().getCurrentUserId(),
                    REQUEST_TAG, this, this);
        }
    }

    private void showConfigurationDialog() {
        DialogFragment confFragmentDialog = new ConfigurationDialogFragment();
        confFragmentDialog.show(getFragmentManager(), "configuration");
    }

    private String buildWarningText(SharedPreferencesController shCon) {
        String warningMessage = getText(R.string.common_attention) + "\n\n";
        if (!shCon.apiRoute().hasApiUrl())
            warningMessage += getText(R.string.warning_no_api_url) + "\n";
        if (!shCon.oAuth2().hasAccessToken() || (shCon.oAuth2().hasAccessToken()
                && shCon.oAuth2().isAccessTokenExpired()))
            warningMessage += getText(R.string.warning_no_access) + "\n\n";
        return warningMessage;
    }
}
