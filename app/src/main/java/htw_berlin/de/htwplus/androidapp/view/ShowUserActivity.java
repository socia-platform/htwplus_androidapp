package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hamnaberg.json.Collection;

import java.util.List;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.ApiError;
import htw_berlin.de.htwplus.androidapp.datamodel.User;
import htw_berlin.de.htwplus.androidapp.util.JsonCollectionHelper;

public class ShowUserActivity extends Activity implements Response.Listener, Response.ErrorListener {

    public static final String REQUEST_TAG = "ShowUserActivity";
    private int accountId;
    private User account;
    private TextView ViewName;
    private TextView ViewEmail;
    private TextView ViewClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);
        ViewName = (TextView) findViewById(R.id.viewName);
        ViewEmail = (TextView) findViewById(R.id.viewEmail);
        ViewClass = (TextView) findViewById(R.id.viewClass);
        accountId = getIntent().getExtras().getInt("accountId");
        account = null;
        ApplicationController.getVolleyController().getUser(accountId, REQUEST_TAG, this, this);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        if (error != null) {
            Toast.makeText(getApplicationContext(), "Error\n", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error is emtpy\n", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponse(Object response) {
        try {
            Collection collection = JsonCollectionHelper.parse(response.toString());
            if (!JsonCollectionHelper.hasError(collection)) {
                List<User> users = JsonCollectionHelper.toUsers(collection);
                if (users.size() == 1) {
                    account = users.get(0);
                    fillStateInformations();
                }
            } else {
                ApiError apiError = JsonCollectionHelper.toError(collection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillStateInformations() {
        if (account != null) {
            ViewName.setText(account.getFirstName() + " " + account.getLastName());
            ViewEmail.setText(account.getEmail());
            ViewClass.setText(account.getStudycourse());
        }
    }
}
