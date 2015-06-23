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

    public void getAllPersons(Object tag, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = ApplicationController.getApiUrl().toString() + "persons";
        final CustomJsonObjectRequest jsonRequest = new CustomJsonObjectRequest(Request.Method.GET, url, new JSONObject(), responseListener, errorListener);
        jsonRequest.setTag(tag);
        mRequestQueue.add(jsonRequest);
    }

    public void addPerson(String name, Object tag, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JSONObject personJson = TESTbuildJsonPost(name);
        String url = ApplicationController.getApiUrl().toString() + "person";
        final CustomJsonObjectRequest jsonRequest = new CustomJsonObjectRequest(Request.Method.POST, url, personJson, responseListener, errorListener);
        jsonRequest.setTag(tag);
        System.out.println(jsonRequest.toString());
        System.out.println(jsonRequest.getBodyContentType());
        mRequestQueue.add(jsonRequest);
    }

    private JSONObject TESTbuildJsonPost(String name)
    {
        JSONObject personJson = new JSONObject();

        try {

            JSONObject collectionSet = new JSONObject();
            collectionSet.put("version", 1.0);
            collectionSet.put("href", ApplicationController.getApiUrl().toString() + "person");


            JSONObject item = new JSONObject();
            item.put("href", ApplicationController.getApiUrl().toString() + "person");
            JSONObject dataSet = new JSONObject();
            dataSet.put("name", name);
            JSONArray dataSetArray = new JSONArray();
            dataSetArray.put(dataSet);
            item.put("data", dataSetArray);
            JSONArray itemArray = new JSONArray();
            itemArray.put(item);
            collectionSet.put("items", itemArray);

            collectionSet.put("template", new JSONObject());

            JSONObject errorSet = new JSONObject();
            errorSet.put("title", "");
            errorSet.put("code", "");
            errorSet.put("message", "");
            collectionSet.put("error", errorSet);

            personJson.put("collection", collectionSet);

        } catch (Exception ex)
        {
            throw new IllegalArgumentException("JSONObject falsch zusammengesetzt");
        }

        /*
             { \"collection\": " +
                "{
                    \"version\": \"1.0\",
                    \"href\": \"localhost:9000/api/stream/7/addPost\",
                    \"items\":
                    [{
                        \"href\": \"localhost:9000/api/stream/7/addPost\",
                        \"data\": [ { \"name\": \"Paul\" } ]
                    }],
                    \"template\": {},
                    \"error\":
                    {
                        \"title\": \"\",
                        \"code\": \"\",
                        \"message\": \"\"
                    }
                 }
              }
         */
        return personJson;
    }
}
