package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import htw_berlin.de.htwplus.androidapp.Application;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

/**
 * Represents the user page with all contacts of the current logged in user.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class UserListViewActivity extends Activity implements
        Response.Listener, Response.ErrorListener {

    /** Request tag, which indicates a http get-request to get all user resources. */
    public static final String VOLLEY_ALL_USERS_REQUEST_TAG = "VolleyAllUsersUserListView";

    /** List of users, which is hold by array adapter. */
    private ArrayList<User> mUserList;

    /** Array adapter, which is hold by list view. */
    private ArrayAdapter<User> mUserAdapter;

    /** List view of the view. */
    private ListView mUserListview;

    /**
     * Called if activity is creating.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);
        initializeListViewComponents();
    }

    /**
     * Called if activity resuming.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (Application.isWorkingState())
            Application.network().getUsers(VOLLEY_ALL_USERS_REQUEST_TAG, this, this);
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
        Application.network().cancelRequest(VOLLEY_ALL_USERS_REQUEST_TAG);
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
        if (response != null) {
            List<User> filteredUsers = filterToContactsOnly((List<User>) response);
            refreshUserData(filteredUsers);
        }
    }

    /**
     * Initializes all list view components.
     */
    private void initializeListViewComponents() {
        mUserList = new ArrayList<User>();
        mUserListview = (ListView) findViewById(R.id.list);
        mUserAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, mUserList);
        mUserListview.setAdapter(mUserAdapter);
        mUserListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ShowUserActivity.class);
                intent.putExtra("accountId", mUserList.get(position).getAccountId());
                UserListViewActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Refreshes the user data of the list view component with the given list of user.
     *
     * @param users List of user to be refreshed
     */
    private void refreshUserData(List<User> users) {
        mUserList.clear();
        for (User user : users)
            mUserList.add(user);
        mUserAdapter.notifyDataSetChanged();
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
