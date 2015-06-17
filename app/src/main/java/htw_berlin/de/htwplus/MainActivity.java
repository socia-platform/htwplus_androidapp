package htw_berlin.de.htwplus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends Activity implements Response.Listener, Response.ErrorListener {
    public static final String REQUEST_TAG = "MainVolleyActivity";
    private String url = "http://10.0.2.2:9000/api/persons";
    private TextView mTextView;
    private Button mButton;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.mTextView);
        mButton = (Button) findViewById(R.id.mButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQueue = VolleyNetworkController.getInstance(this.getApplicationContext()).getRequestQueue();
        JSONObject json = new JSONObject();
        System.out.println(json.toString());
        final CustomJsonObjectRequest jsonRequest = new CustomJsonObjectRequest(Request.Method.GET, url, json, this, this);
        jsonRequest.setTag(REQUEST_TAG);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueue.add(jsonRequest);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mTextView.setText(error.getMessage());
    }

    @Override
    public void onResponse(Object response) {
        mTextView.setText("Response is: " + response);
        try {
            response = (JSONObject) response;
            //mTextView.setText(mTextView.getText() + "\n\n" + ((JSONObject) response).getString("Name"));
            Map<String, Object> map = CustomJsonObjectRequest.toMap((JSONObject) response);
            mTextView.setText(mTextView.getText() + "\n\n" + map.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
