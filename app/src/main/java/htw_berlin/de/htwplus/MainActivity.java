package htw_berlin.de.htwplus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Map;


/*
0.0.2.1	    Router/gateway address
10.0.2.2	Special alias to your host loopback interface (i.e., 127.0.0.1 on your development machine)
10.0.2.3	First DNS server
10.0.2.15	The emulated device's own network/ethernet interface
127.0.0.1	The emulated device's own loopback interface
 */

public class MainActivity extends Activity implements Response.Listener, Response.ErrorListener, View.OnClickListener {
    public static final String REQUEST_TAG = "MainVolleyActivity";
    private String url = "http://192.168.0.212:9000/api/persons";
    private TextView mTextView;
    private Button mButton;
    private Button mButtonPost;
    //private RequestQueue mQueue;
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
        if(v == mButton) {
            VolleyNetworkController.getInstance().getUser(1, REQUEST_TAG, this, this);
        } else if(v == mButtonPost) {
            //VolleyNetworkController.getInstance().addPerson(mEditText.getText().toString(), REQUEST_TAG, this, this);
            VolleyNetworkController.getInstance().addPerson("Xavier", REQUEST_TAG, this, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyNetworkController.getInstance().cancelRequest(REQUEST_TAG);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mTextView.setText(error.getMessage());
    }

    @Override
    public void onResponse(Object response) {
        mTextView.setText("Response is: " + response);
        try {
            // response = (JSONObject) response;
            //mTextView.setText(mTextView.getText() + "\n\n" + ((JSONObject) response).getString("Name"));
            Map<String, Object> map = CustomJsonObjectRequest.toMap((JSONObject) response);
            mTextView.setText(mTextView.getText() + "\n\n" + map.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
