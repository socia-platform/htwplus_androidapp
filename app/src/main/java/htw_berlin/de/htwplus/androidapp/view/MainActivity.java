package htw_berlin.de.htwplus.androidapp.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.SharedPreferencesController;
import htw_berlin.de.htwplus.androidapp.VolleyNetworkController;
import htw_berlin.de.htwplus.androidapp.view.dialog.ConfigurationDialogFragment;


public class MainActivity extends FragmentActivity implements ConfigurationDialogFragment.ConfigurationDialogListener {
    public static final String REQUEST_TAG = "MainVolleyActivity";
    private TextView mTextView;
    private Dialog mAuthDialog;
    private Button mPostsButton;
    private Button mGroupsButton;
    private Button mContactsButton;
    private TextView mMainTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPostsButton = (Button) findViewById(R.id.postButton);
        mGroupsButton = (Button) findViewById(R.id.groupsButton);
        mContactsButton = (Button) findViewById(R.id.contactsButton);
        mMainTextView = (TextView) findViewById(R.id.mainTextView);

        mPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostListViewActivity.class);
                startActivity(intent);
            }
        });

        mContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserListViewActivity.class);
                startActivity(intent);
            }
        });

        mGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), Grou.class);
                //startActivity(intent);
            }
        });

        mMainTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfigurationDialog();
            }
        });

        mAuthDialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mAuthDialog.setContentView(R.layout.oauth2_dialog);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!ApplicationController.getInstance().isWorkingState()) {
            String warningText = buildWarningText(ApplicationController.getSharedPrefController());
            mMainTextView.setText(warningText);
            mMainTextView.setTypeface(null, Typeface.ITALIC);
            mMainTextView.setClickable(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyNetworkController.getInstance().cancelRequest(REQUEST_TAG);
    }

    @Override
    public void onConfigurationDialogPositiveClick(DialogFragment dialog, URL apiUrl) {
        ApplicationController.getSharedPrefController().setApiUrl(apiUrl);
        Toast.makeText(this, R.string.api_url_saved, Toast.LENGTH_LONG).show();
    }

    private void showConfigurationDialog() {
        DialogFragment confFragmentDialog = new ConfigurationDialogFragment();
        confFragmentDialog.show(getFragmentManager(), "configuration");
    }

    private String buildWarningText(SharedPreferencesController shCon) {
        String warningMessage = "Achtung!\n\n";
        if (!shCon.hasApiUrl())
            warningMessage += getText(R.string.warning_no_api_url) + "\n";
        if (!shCon.hasAccessToken())
            warningMessage += getText(R.string.warning_no_access) + "\n\n";
        warningMessage += "[ " + getText(R.string.click_info_open_configuration) + " ]";
        return warningMessage;
    }
}
