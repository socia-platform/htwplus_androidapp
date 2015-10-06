package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.hamnaberg.funclite.Optional;
import net.hamnaberg.json.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;
import htw_berlin.de.htwplus.androidapp.util.JsonCollectionHelper;

public class VolleyNetworkController {
    private static VolleyNetworkController mInstance;
    private RequestQueue mRequestQueue;

    private VolleyNetworkController(Context context) {
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

    public void cancelRequest(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public void getAccessToken(Object tag, Response.Listener<String> responseListener,
                               Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("client_secret", Application.preferences().oAuth2().getClientSecret());
        params.put("code", Application.preferences().oAuth2().getAuthorizationToken());
        params.put("grant_type", "authorization_code");
        params.put("client_id", Application.preferences().oAuth2().getClientId());
        String url = Application.preferences().apiRoute().token(params);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                        responseListener, errorListener);
        stringRequest.setTag(tag);
        mRequestQueue.add(stringRequest);
    }

    public void refreshAccessToken(Object tag, Response.Listener<String> responseListener,
                                   Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("client_secret", Application.preferences().oAuth2().getClientSecret());
        params.put("refresh_token", Application.preferences().oAuth2().getRefreshToken());
        params.put("grant_type", "refresh_token");
        params.put("client_id", Application.preferences().oAuth2().getClientId());
        String url = Application.preferences().apiRoute().token(params);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                                        responseListener, errorListener);
        stringRequest.setTag(tag);
        mRequestQueue.add(stringRequest);
    }

    public void getUser(long userId, Object tag, Response.Listener<User> responseListener,
                        Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", Application.preferences().oAuth2().getAccessToken());
        String url = Application.preferences().apiRoute().user(userId, params);
        final CollectionJsonRequest collJsonGetRequest =
                new CollectionJsonRequest(Request.Method.GET, url, User.class, null,
                                          responseListener, errorListener);
        collJsonGetRequest.setTag(tag);
        mRequestQueue.add(collJsonGetRequest);
    }

    public void getUsers(Object tag, Response.Listener<User> responseListener,
                         Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", Application.preferences().oAuth2().getAccessToken());
        String url = Application.preferences().apiRoute().users(params);
        final CollectionJsonRequest collJsonGetRequest =
                new CollectionJsonRequest(Request.Method.GET, url, User.class, null,
                                          responseListener, errorListener);
        collJsonGetRequest.setTag(tag);
        mRequestQueue.add(collJsonGetRequest);
    }

    public void getPost(long postId, Object tag, Response.Listener<User> responseListener,
                        Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", Application.preferences().oAuth2().getAccessToken());
        String url = Application.preferences().apiRoute().post(postId, params);
        final CollectionJsonRequest collJsonGetRequest =
                new CollectionJsonRequest(Request.Method.GET, url, Post.class, null,
                        responseListener, errorListener);
        collJsonGetRequest.setTag(tag);
        mRequestQueue.add(collJsonGetRequest);
    }

    public void getPosts(Object tag, Response.Listener<Post> responseListener,
                         Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", Application.preferences().oAuth2().getAccessToken());
        String url = Application.preferences().apiRoute().posts(params);
        final CollectionJsonRequest collJsonGetRequest =
                new CollectionJsonRequest(Request.Method.GET, url, Post.class, null,
                                          responseListener, errorListener);
        collJsonGetRequest.setTag(tag);
        mRequestQueue.add(collJsonGetRequest);
    }

    public void addPost(String content, Optional<Long> accountId, Optional<Long> ownerId,
                        Optional<Long> parentId, Optional<Long> groupId, Object tag,
                        Response.Listener<JSONObject> responseListener,
                        Response.ErrorListener errorListener) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("access_token", Application.preferences().oAuth2().getAccessToken());
            String resourceUrl = Application.preferences().apiRoute().user(accountId.get(), params);
            Collection collectionJson = JsonCollectionHelper.buildPost(
                    resourceUrl, content, accountId, ownerId, parentId, groupId);
            String url = Application.preferences().apiRoute().posts(params);
            final CollectionJsonRequest collJsonPostRequest =
                    new CollectionJsonRequest(Request.Method.POST, url, Post.class, collectionJson,
                            responseListener, errorListener);
            collJsonPostRequest.setTag(tag);
            mRequestQueue.add(collJsonPostRequest);
        } catch (JSONException e) {
            Log.d("VolleyNetworkController", "Exception Occured: ", e);
        }
    }

}
