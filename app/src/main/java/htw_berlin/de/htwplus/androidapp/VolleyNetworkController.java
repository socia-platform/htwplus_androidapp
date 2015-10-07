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

/**
 * Represents a controller, which manage all http requests for HTWPlus RESTful API.<br />
 * <br />
 * This is a singleton class and should use only in combination with application singleton.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class VolleyNetworkController {

    /** Static instance of this class. */
    private static VolleyNetworkController mInstance;

    /** Request queue of all http requests. */
    private RequestQueue mRequestQueue;

    /**
     * Creates a new volley http controller with the given context.
     *
     * @param context Context of controller
     */
    private VolleyNetworkController(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Returns the unique instance of this class, if it not instantiate yet so a new instance
     * will be created with the given context.
     *
     * @param context Context of preferences
     *
     * @return Unique instance of this class.
     */
    public static synchronized VolleyNetworkController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyNetworkController(context);
        }
        return mInstance;
    }

    /**
     * Returns the unique instance of this class.
     *
     * @return Unique instance of this class.
     *
     * @throws IllegalStateException Throws if the instance is not instantiate yet.
     */
    public static synchronized VolleyNetworkController getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(VolleyNetworkController.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        return mInstance;
    }

    /**
     * Cancels all requests those are matched with the given tag.
     *
     * @param tag Tag to be match
     */
    public void cancelRequest(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    /**
     * Creates and sends a post-request to get a access token with the given tag, response
     * listener and error listener.
     *
     * @param tag Tag to be set for the request
     * @param responseListener Response listener to be set for callback
     * @param errorListener Error listener to be set for callback
     */
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

    /**
     * Creates and sends a post-request to get a refresh token with the given tag, response
     * listener and error listener.
     *
     * @param tag Tag to be set for the request
     * @param responseListener Response listener to be set for callback
     * @param errorListener Error listener to be set for callback
     */
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

    /**
     * Creates and sends a get-request to get a single user resource with the given user id, tag,
     * response listener and error listener.
     *
     * @param userId User id to be set for target user resource
     * @param tag Tag to be set for the request
     * @param responseListener Response listener to be set for callback
     * @param errorListener Error listener to be set for callback
     */
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

    /**
     * Creates and sends a get-request to get all user resources with the given tag, response
     * listener and error listener.
     *
     * @param tag Tag to be set for the request
     * @param responseListener Response listener to be set for callback
     * @param errorListener Error listener to be set for callback
     */
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

    /**
     * Creates and sends a get-request to get a single posting resource with the given post id, tag,
     * response listener and error listener.
     *
     * @param postId Posting id to be set for target user resource
     * @param tag Tag to be set for the request
     * @param responseListener Response listener to be set for callback
     * @param errorListener Error listener to be set for callback
     */
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

    /**
     * Creates and sends a get-request to get all posting resources with the given tag, response
     * listener and error listener.
     *
     * @param tag Tag to be set for the request
     * @param responseListener Response listener to be set for callback
     * @param errorListener Error listener to be set for callback
     */
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

    /**
     * Creates and sends a post-request to add a single posting in HTWPlus with the given
     * content, users's account id, owner id, parent id, group id, tag, response listener and
     * error listener.
     * <br /><br/>
     * Should there be normal posting so account id and owner id must be the user id, who want
     * to send, the other id's must be set to null. <br />
     * Should there be a comment posting so account id and owner id must be the user id, who want
     * to send, parent id must be the id of posting to which posting should be answered, group id
     * must be null. <br />
     * Should there be a group comment posting so account id and owner id must be the user id,
     * who want to send, parent id must be the id of posting to which group posting should be
     * answered, the group id must be the id of group in which the comment should be created.<br />
     *
     * @param content Content of new posting
     * @param accountId Account id of new posting
     * @param ownerId Owner id of new posting
     * @param parentId Parent id of new posting
     * @param groupId Group id of new posting
     * @param tag Tag to be set for the request
     * @param responseListener Response listener to be set for callback
     * @param errorListener Error listener to be set for callback
     */
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
