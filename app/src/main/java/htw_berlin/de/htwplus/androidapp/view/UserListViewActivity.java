package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import htw_berlin.de.htwplus.androidapp.Application;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

public class UserListViewActivity extends Activity implements Response.Listener, Response.ErrorListener {

    private ArrayList<User> mlist;
    private ArrayAdapter<User> mAdapter;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);
        initializeListViewComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Application.isWorkingState())
            Application.getVolleyController().getUsers(this, this, this);
        else
            Toast.makeText(getApplicationContext(), R.string.common_error_no_connection,
                    Toast.LENGTH_LONG).show();
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
        if (response != null) {
            List<User> filteredUsers = filterToContactsOnly((List<User>) response);
            refreshUserData(filteredUsers);
        }
    }

    private void initializeListViewComponents() {
        mlist = new ArrayList<User>();
        listview = (ListView) findViewById(R.id.list);
        mAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, mlist);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ShowUserActivity.class);
                intent.putExtra("accountId", mlist.get(position).getAccountId());
                UserListViewActivity.this.startActivity(intent);
            }
        });
    }

    private void refreshUserData(List<User> users) {
        mlist.clear();
        for (User user : users)
            mlist.add(user);
        mAdapter.notifyDataSetChanged();
    }

    private List<User> filterToContactsOnly(List<User> users) {
        List<User> filteredUsers = new ArrayList<User>();
        for (User user : users) {
            if (user.getAccountId() != Application.preferences().oAuth2().getCurrentUserId())
                filteredUsers.add(user);
        }
        return filteredUsers;
    }
}
