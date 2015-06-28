package htw_berlin.de.htwplus;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;

public class VolleyNetworkController {

    private static VolleyNetworkController mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;

    private VolleyNetworkController(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized VolleyNetworkController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyNetworkController(context);
        }
        return mInstance;
    }

    public static synchronized VolleyNetworkController getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(VolleyNetworkController.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mContext.getCacheDir());
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public void cancelRequest(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public void getUser(long userId, Object tag, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = ApplicationController.getApiUrl().toString() + "users/" + userId;
        final CustomJsonObjectRequest jsonRequest = new CustomJsonObjectRequest(Request.Method.GET, url, new JSONObject(), responseListener, errorListener);
        jsonRequest.setTag(tag);
        mRequestQueue.add(jsonRequest);
    }

    public void getPost(long postId, Object tag, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        throw new UnsupportedOperationException("getPost() still need to be implemented.");
    }

    public void addPost() {
        throw new UnsupportedOperationException("addPost() still need to be implemented.");
    }

}
