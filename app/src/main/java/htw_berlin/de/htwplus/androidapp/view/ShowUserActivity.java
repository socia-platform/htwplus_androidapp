package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import htw_berlin.de.htwplus.androidapp.Application;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

/**
 * Represents the detail view of a user.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class ShowUserActivity extends Activity implements
        Response.Listener, Response.ErrorListener {

    /** Request tag, which indicates a http get-request to get a single user resource. */
    public static final String VOLLEY_ONE_USER_REQUEST_TAG = "VolleyOneUserShowUser";

    /** Id of the user, which information should displayed. */
    private int mAccountId;

    /** User object, which information should displayed. */
    private User mAccount;

    /** User fullname text view of the view. */
    private TextView mUserNameTextView;

    /** User email text view of the view. */
    private TextView mUserEmailTextView;

    /** User study course text view of the view. */
    private TextView mUserStudyCourseTextView;

    /**
     * Called if activity is creating.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);
        mUserNameTextView = (TextView) findViewById(R.id.userNameTextView);
        mUserEmailTextView = (TextView) findViewById(R.id.userEmailTextView);
        mUserStudyCourseTextView = (TextView) findViewById(R.id.userStudyCourseTextView);
        mAccountId = getIntent().getExtras().getInt("accountId");
        mAccount = null;
    }

    /**
     * Called if activity resuming.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (Application.isWorkingState())
            Application.network().getUser(mAccountId, VOLLEY_ONE_USER_REQUEST_TAG, this, this);
        else
            Toast.makeText(getApplicationContext(), R.string.common_error_no_connection,
                    Toast.LENGTH_LONG).show();
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
        List<User> filteredUsers = filterToContactsOnly((List<User>) response);
        if (filteredUsers.size() == 1) {
            mAccount = filteredUsers.get(0);
            fillStateInformations();
        }
    }

    /**
     * Fills all ui components of the view with content depending on the current state.
     */
    private void fillStateInformations() {
        if (mAccount != null) {
            mUserNameTextView.setText(mAccount.getFirstName() + " " + mAccount.getLastName());
            mUserEmailTextView.setText(mAccount.getEmail());
            mUserStudyCourseTextView.setText(mAccount.getStudycourse());
        }
    }

    /**
     * Filters the own user object out, which is current logged in.
     *
     * @param users List of users
     *
     * @return Filtered list of users without the own user object.
     */
    private List<User> filterToContactsOnly(List<User> users) {
        List<User> filteredUsers = new ArrayList<User>();
        for (User user : users) {
            if (user.getAccountId() != Application.preferences().oAuth2().getCurrentUserId())
                filteredUsers.add(user);
        }
        return filteredUsers;
    }
}
