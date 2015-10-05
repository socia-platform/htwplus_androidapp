package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

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
            account = ((List<User>)response).get(0);
            fillStateInformations();
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
