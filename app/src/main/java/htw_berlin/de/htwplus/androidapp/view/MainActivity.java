package htw_berlin.de.htwplus.androidapp.view;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.SharedPreferencesController;
import htw_berlin.de.htwplus.androidapp.view.dialog.ConfigurationDialogFragment;

public class MainActivity extends FragmentActivity implements ConfigurationDialogFragment.ConfigurationDialogListener {
    private Button mConfigButton;
    private Button mPostsButton;
    private Button mContactsButton;
    private TextView mMainTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConfigButton = (Button) findViewById(R.id.configurationButton);
        mPostsButton = (Button) findViewById(R.id.postButton);
        mContactsButton = (Button) findViewById(R.id.contactsButton);
        mMainTextView = (TextView) findViewById(R.id.mainTextView);
        initiateButtonClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillStateInformations();
    }

    @Override
    public  void onConfigurationDialogDismissed() {
        if (!ApplicationController.getInstance().isWorkingState()) {
            String warningText = buildWarningText(ApplicationController.getSharedPrefController());
            mMainTextView.setText(warningText);
        } else
            mMainTextView.setText("");
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
        if (!ApplicationController.getInstance().isWorkingState()) {
            String warningText = buildWarningText(ApplicationController.getSharedPrefController());
            mMainTextView.setText(warningText);
        } else
            mMainTextView.setText("");
    }

    private void showConfigurationDialog() {
        DialogFragment confFragmentDialog = new ConfigurationDialogFragment();
        confFragmentDialog.show(getFragmentManager(), "configuration");
    }

    private String buildWarningText(SharedPreferencesController shCon) {
        String warningMessage = getText(R.string.common_attention) + "\n\n";
        if (!shCon.hasApiUrl())
            warningMessage += getText(R.string.warning_no_api_url) + "\n";
        if (!shCon.oAuth2().hasAccessToken() || (shCon.oAuth2().hasAccessToken()
                && shCon.oAuth2().isAccessTokenExpired()))
            warningMessage += getText(R.string.warning_no_access) + "\n\n";
        return warningMessage;
    }
}
