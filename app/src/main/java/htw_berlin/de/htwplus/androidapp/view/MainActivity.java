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
import htw_berlin.de.htwplus.androidapp.SharedPreferencesProxy;
import htw_berlin.de.htwplus.androidapp.datamodel.User;
import htw_berlin.de.htwplus.androidapp.view.dialog.ConfigurationDialogFragment;

/**
 * Represents the main view.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class MainActivity extends FragmentActivity implements Response.Listener,
        Response.ErrorListener, ConfigurationDialogFragment.ConfigurationDialogListener {

    /** Request tag, which indicates a http get-request to get a single user resource. */
    public static final String VOLLEY_ONE_USER_REQUEST_TAG = "VolleyOneUserMain";

    /** Configuration button of the view. */
    private Button mConfigButton;

    /** Posting button of the view. */
    private Button mPostsButton;

    /** Contacts button of the view. */
    private Button mContactsButton;

    /** Main text view of the view. */
    private TextView mMainTextView;

    /** Current logged in user. */
    private User mCurrentUser;

    /**
     * Called if activity is creating.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurrentUser = null;
        mConfigButton = (Button) findViewById(R.id.configurationButton);
        mPostsButton = (Button) findViewById(R.id.postButton);
        mContactsButton = (Button) findViewById(R.id.contactsButton);
        mMainTextView = (TextView) findViewById(R.id.mainTextView);
        initiateButtonClickListeners();
    }

    /**
     * Called if activity resuming.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateState();
    }

    /**
     * Called if activity is stopping.
     */
    @Override
    public void onStop() {
        super.onStop();
        Application.network().cancelRequest(VOLLEY_ONE_USER_REQUEST_TAG);
    }

    /**
     * Called if configuration dialog dismissed.
     */
    @Override
    public  void onConfigurationDialogDismissed() {
        updateState();
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
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Called if a http response is received, which is ok.
     *
     * @param response
     */
    @Override
    public void onResponse(Object response) {
        if (((List<User>)response).size() == 1) {
            mCurrentUser = ((List<User>)response).get(0);
            fillStateInformations();
        }
    }

    /**
     * Initiates listeners for all buttons of the view.
     */
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

    /**
     * Called if posting button of the view was clicked.
     */
    private void onPostsButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), PostListViewActivity.class);
        startActivity(intent);
    }

    /**
     * Called if contacts button of the view was clicked.
     */
    private void onContactsButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), UserListViewActivity.class);
        startActivity(intent);
    }

    /**
     * Called if configuration button of the view was clicked.
     */
    private void onConfigurationButtonClick() {
        showConfigurationDialog();
    }

    /**
     * Fills all ui components of the view with content depending on the current state.
     */
    private void fillStateInformations() {
        if (!Application.getInstance().isWorkingState()) {
            String warningText = buildWarningText(Application.preferences());
            mMainTextView.setText(warningText);
        } else if (mCurrentUser != null) {
            String welcomeText = getText(R.string.main_welcome).toString();
            welcomeText += ' ' + mCurrentUser.getFirstName() + ' ' + mCurrentUser.getLastName();
            mMainTextView.setText(welcomeText);
        } else
            mMainTextView.setText("");
    }

    /**
     * Updates the state of activity.
     */
    private void updateState() {
        fillStateInformations();
        if (Application.getInstance().isWorkingState()) {
            Application.network().getUser(
                    Application.preferences().oAuth2().getCurrentUserId(),
                    VOLLEY_ONE_USER_REQUEST_TAG, this, this);
        }
    }

    /**
     * Activates and displays the configuration dialog.
     */
    private void showConfigurationDialog() {
        DialogFragment confFragmentDialog = new ConfigurationDialogFragment();
        confFragmentDialog.show(getFragmentManager(), "configuration");
    }

    /**
     * Assembles a warning text depending on the app state.
     *
     * @param shCon Shared preference proxy for access of app attributes
     *
     * @return Warning text depending on the app state.
     */
    private String buildWarningText(SharedPreferencesProxy shCon) {
        String warningMessage = getText(R.string.common_attention) + "\n\n";
        if (!shCon.apiRoute().hasApiUrl())
            warningMessage += getText(R.string.warning_no_api_url) + "\n";
        if (!shCon.oAuth2().hasAccessToken() || (shCon.oAuth2().hasAccessToken()
                && shCon.oAuth2().isAccessTokenExpired()))
            warningMessage += getText(R.string.warning_no_access) + "\n\n";
        if ((shCon.oAuth2().getClientId().isEmpty()) || (shCon.oAuth2().getClientSecret().isEmpty())
                || (shCon.oAuth2().getAuthCallBackURI().isEmpty()))
            warningMessage += getText(R.string.warning_no_oauth2_data) + "\n\n";
        return warningMessage;
    }
}
