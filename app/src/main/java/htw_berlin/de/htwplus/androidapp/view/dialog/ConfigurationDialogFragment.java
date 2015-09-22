package htw_berlin.de.htwplus.androidapp.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import htw_berlin.de.htwplus.androidapp.R;

/**
 * Created by tino on 22.09.15.
 */
public class ConfigurationDialogFragment extends DialogFragment implements View.OnClickListener {

    public interface ConfigurationDialogListener {
        public void onConfigurationDialogPositiveClick(DialogFragment dialog, URL apiUrl);
    }

    private ConfigurationDialogListener mListener;
    private View mDialogView;
    private EditText mApiUrlEditText;
    private Button mConfirmButton;

    // Override the Fragment.onAttach() method to instantiate the ConfigurationDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ConfigurationDialogListener so we can send events to the host
            mListener = (ConfigurationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ConfigurationDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDialogView = inflater.inflate(R.layout.configuration_dialog, container);
        getDialog().setTitle(R.string.configuration_title);
        setCancelable(false);

        mApiUrlEditText = (EditText) mDialogView.findViewById(R.id.apiUrlEditText);
        mConfirmButton = (Button) mDialogView.findViewById(R.id.confirmButton);

        mConfirmButton.setOnClickListener(this);

        return mDialogView;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mConfirmButton)) {
            try {
                URL apiUrl = new URL(mApiUrlEditText.getText().toString());
                if (isHostReachable(apiUrl)) {
                    mListener.onConfigurationDialogPositiveClick(ConfigurationDialogFragment.this, apiUrl);
                    dismiss();
                }
                else
                    Toast.makeText(getActivity(), R.string.error_host_unreachable, Toast.LENGTH_LONG).show();
            } catch (MalformedURLException mex) {
                Toast.makeText(getActivity(), R.string.error_api_url_invalid, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isHostReachable(URL hostUrl) {
        boolean isReachable = true;
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec("ping -c 1 " + hostUrl.getHost());
            int mPingResult = proc.waitFor();
            if (mPingResult != 0)
                isReachable = false;
        } catch (Exception ex) {
            isReachable = false;
        }
        return isReachable;
    }
}
