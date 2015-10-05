package htw_berlin.de.htwplus.androidapp;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.hamnaberg.funclite.Optional;
import net.hamnaberg.json.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;
import htw_berlin.de.htwplus.androidapp.util.JsonCollectionHelper;

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
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public void cancelRequest(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public void getAccessToken(String authToken, Object tag, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        SharedPreferencesController shCon = ApplicationController.getSharedPrefController();
        String url = shCon.getApiUrl().toString();
        url += "oauth2/token?client_id=" + shCon.oAuth2().getClientId();
        url += "&grant_type=authorization_code&code=" + authToken;
        url += "&client_secret=" + shCon.oAuth2().getClientSecret();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener,
                errorListener);
        mRequestQueue.add(stringRequest);
    }

    public void refreshAccessToken(String accessToken, String refreshToken, Object tag,
                                   Response.Listener<String> responseListener,
                                   Response.ErrorListener errorListener) {
        SharedPreferencesController shCon = ApplicationController.getSharedPrefController();
        String url = shCon.getApiUrl().toString();
        url += "oauth2/token?client_id=" + shCon.oAuth2().getClientId();
        url += "&grant_type=refresh_token&refresh_token=" + refreshToken;
        url += "&client_secret=" + shCon.oAuth2().getClientSecret();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                                        url,
                                                        responseListener,
                                                        errorListener);
        mRequestQueue.add(stringRequest);
    }

    public void getUser(long userId, Object tag, Response.Listener<User> responseListener, Response
            .ErrorListener errorListener) {
        String accessToken = ApplicationController.getSharedPrefController().oAuth2().getAccessToken();
        String apiUrl = ApplicationController.getSharedPrefController().getApiUrl().toString();
        String url = apiUrl + "users/" + userId + "?access_token=" + accessToken;
        final CollectionJsonRequest collJsonGetRequest =
                new CollectionJsonRequest(Request.Method.GET, url, User.class, null,
                                          responseListener, errorListener);
        collJsonGetRequest.setTag(tag);
        mRequestQueue.add(collJsonGetRequest);
    }

    public void getUsers(Object tag, Response.Listener<User> responseListener,
                         Response.ErrorListener errorListener) {
        String accessToken = ApplicationController.getSharedPrefController().oAuth2().getAccessToken();
        String apiUrl = ApplicationController.getSharedPrefController().getApiUrl().toString();
        String url = apiUrl + "users" + "?access_token=" + accessToken;
        final CollectionJsonRequest collJsonGetRequest =
                new CollectionJsonRequest(Request.Method.GET, url, User.class, null,
                                          responseListener, errorListener);
        collJsonGetRequest.setTag(tag);
        mRequestQueue.add(collJsonGetRequest);
    }

    public void getPost(long postId, Object tag, Response.Listener<Post> responseListener,
                        Response.ErrorListener errorListener) {
        String accessToken = ApplicationController.getSharedPrefController().oAuth2().getAccessToken();
        String apiUrl = ApplicationController.getSharedPrefController().getApiUrl().toString();
        String url = apiUrl + "posts/" + postId + "?access_token=" + accessToken;
        final CollectionJsonRequest collJsonGetRequest =
                new CollectionJsonRequest(Request.Method.GET, url, Post.class, null,
                                          responseListener, errorListener);
        collJsonGetRequest.setTag(tag);
        mRequestQueue.add(collJsonGetRequest);
    }

    public void getPostsFromNewsstream(Object tag, Response.Listener<Post> responseListener,
                                       Response.ErrorListener errorListener) {
        String accessToken = ApplicationController.getSharedPrefController().oAuth2().getAccessToken();
        String apiUrl = ApplicationController.getSharedPrefController().getApiUrl().toString();
        String url = apiUrl + "posts" + "?access_token=" + accessToken;
        final CollectionJsonRequest collJsonGetRequest =
                new CollectionJsonRequest(Request.Method.GET, url, Post.class, null,
                                          responseListener, errorListener);
        collJsonGetRequest.setTag(tag);
        mRequestQueue.add(collJsonGetRequest);
    }

    public void addPost(String content, Optional<Long> accountId, Optional<Long> ownerId,
                        Optional<Long> parentId, Optional<Long> groupId, Object tag,
                        Response.Listener<JSONObject> responseListener,
                        Response.ErrorListener errorListener) throws JSONException {
        Collection collectionJson = JsonCollectionHelper.buildPost(content, accountId, ownerId,
                                                                   parentId, groupId);
        String apiUrl = ApplicationController.getSharedPrefController().getApiUrl().toString();
        String accessToken = ApplicationController.getSharedPrefController().oAuth2().getAccessToken();
        String url = apiUrl + "posts" + "?access_token=" + accessToken;
        final CollectionJsonRequest collJsonPostRequest =
                new CollectionJsonRequest(Request.Method.POST, url, Post.class, collectionJson,
                        responseListener, errorListener);
        collJsonPostRequest.setTag(tag);
        mRequestQueue.add(collJsonPostRequest);
    }

}
