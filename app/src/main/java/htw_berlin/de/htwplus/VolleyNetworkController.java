package htw_berlin.de.htwplus;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

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
}
