package htw_berlin.de.htwplus.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hamnaberg.json.Collection;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import htw_berlin.de.htwplus.CustomJsonObjectRequest;
import htw_berlin.de.htwplus.R;
import htw_berlin.de.htwplus.VolleyNetworkController;
import htw_berlin.de.htwplus.datamodel.ApiError;
import htw_berlin.de.htwplus.datamodel.User;
import htw_berlin.de.htwplus.util.JsonCollectionHelper;


public class MainActivity extends Activity implements Response.Listener, Response.ErrorListener, View.OnClickListener {
    public static final String REQUEST_TAG = "MainVolleyActivity";
    private TextView mTextView;
    private Button mButton;
    private Button mButtonPost;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.mTextView);
        mButton = (Button) findViewById(R.id.mButton);
        mButtonPost = (Button) findViewById(R.id.mButtonPost);
        mButton.setOnClickListener(this);
        mButtonPost.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.mEditText);
        //mQueue = VolleyNetworkController.getInstance(this.getApplicationContext()).getRequestQueue();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        JSONObject json = new JSONObject();
        final CustomJsonObjectRequest jsonRequest = new CustomJsonObjectRequest(Request.Method.GET, url, json, this, this);
        jsonRequest.setTag(REQUEST_TAG);
        System.out.println(json.toString());

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueue.add(jsonRequest);
            }
        });
        */
    }

    @Override
    public void onClick(View v) {
/*        if(v == mButton) {
            ApplicationController.getVolleyController().getUser(1, REQUEST_TAG, this, this);
        } else if(v == mButtonPost) {
            try {
                String postMessage = mEditText.getText().toString();
                if (!postMessage.isEmpty())
                    ApplicationController.getVolleyController().addPost(postMessage, 1, 1, null, null, REQUEST_TAG, this, this);
                else
                    Toast.makeText(getApplicationContext(), "Bitte Post-Message eingeben!", Toast.LENGTH_LONG).show();
            } catch (JSONException jex) {
                Toast.makeText(getApplicationContext(), "JSON parse Exception!\nSiehe konsole!", Toast.LENGTH_LONG).show();
                jex.printStackTrace();
            }
        }*/

        /*
        Intent intent = new Intent(this, PostListViewActivity.class);
        MainActivity.this.startActivity(intent);
        */

        Intent intent = new Intent(this, UserListViewActivity.class);
        MainActivity.this.startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyNetworkController.getInstance().cancelRequest(REQUEST_TAG);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mTextView.setText("Error\n");
        if (error != null) {
            String errorMessage = error.getMessage() != null ? error.getMessage() : "";
            mTextView.setText(mTextView.getText() + errorMessage);
            if (errorMessage.contains("org.json.JSONException: End of input at character 0"))
                mTextView.setText(mTextView.getText() + "\nVolley erwartet hier eine Antwort " +
                        "mit JSON im Body was aber bei einem POST Request keinen Sinn macht.\n" +
                        "Hier muss die Methode parseNetworkResponse() überschrieben werden.\n" +
                        "Der POST-Request wurde aber trotzdem erfolgreich vom Server verarbeitet.");
        }
    }

    @Override
    public void onResponse(Object response) {
        mTextView.setText("Response is: " + response);
        try {
            // response = (JSONObject) response;
            //mTextView.setText(mTextView.getText() + "\n\n" + ((JSONObject) response).getString("Name"));
            Map<String, Object> map = CustomJsonObjectRequest.toMap((JSONObject) response);
            mTextView.setText(mTextView.getText() + "\n\n" + map.toString());

            Collection collection = JsonCollectionHelper.parse(response.toString());
            if (!JsonCollectionHelper.hasError(collection)) {
                List<User> users = JsonCollectionHelper.toUsers(collection);
                mTextView.setText(mTextView.getText() + "\n\nUsers:\n");
                for (User user : users)
                    mTextView.setText(mTextView.getText() + user.getFirstName() + " " +
                            user.getLastName() + ", " + user.getEmail() + ", " +
                            user.getStudycourse());
            } else {
                ApiError apiError = JsonCollectionHelper.toError(collection);
                mTextView.setText(mTextView.getText() + "\n\nError!\n");
                mTextView.setText(mTextView.getText() + apiError.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
