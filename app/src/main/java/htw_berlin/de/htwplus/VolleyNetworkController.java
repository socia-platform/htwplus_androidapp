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

import net.hamnaberg.funclite.Optional;
import net.hamnaberg.json.Collection;
import net.hamnaberg.json.Item;
import net.hamnaberg.json.Link;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Query;
import net.hamnaberg.json.Template;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import htw_berlin.de.htwplus.view.UserListViewActivity;

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

    public void getUsers(Object tag, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = ApplicationController.getApiUrl().toString() + "users";
        final CustomJsonObjectRequest jsonRequest = new CustomJsonObjectRequest(Request.Method.GET, url, new JSONObject(), responseListener, errorListener);
        jsonRequest.setTag(tag);
        mRequestQueue.add(jsonRequest);
    }

    public void getPost(long postId, Object tag, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = ApplicationController.getApiUrl().toString() + "posts/" + postId;
        final CustomJsonObjectRequest jsonRequest = new CustomJsonObjectRequest(Request.Method.GET, url, new JSONObject(), responseListener, errorListener);
        jsonRequest.setTag(tag);
        mRequestQueue.add(jsonRequest);
    }

    public void getPostsFromNewsstream(Object tag, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        String url = ApplicationController.getApiUrl().toString() + "posts";
        final CustomJsonObjectRequest jsonRequest = new CustomJsonObjectRequest(Request.Method.GET, url, new JSONObject(), responseListener, errorListener);
        jsonRequest.setTag(tag);
        mRequestQueue.add(jsonRequest);
    }

    public void addPost(String content, Optional<Long> accountId, Optional<Long> ownerId, Optional<Long> parentId, Optional<Long> groupId, Object tag,
                        Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) throws JSONException {
        URI resourceUri = URI.create(ApplicationController.getApiUrl() + "users/" + String.valueOf(accountId.get()));
        List<Link> links = new ArrayList<Link>();
        List<Query> queries = new ArrayList<Query>();

        List<Item> items = new ArrayList<Item>();
        Property contentProp = Property.value("content", Optional.some("The content."), content);
        Property accIdProp = null;
        if (Optional.fromNullable(accountId).isNone())
            accIdProp = Property.value("account_id", Optional.some("The account id."), "");
        else
            accIdProp = Property.value("account_id", Optional.some("The account id."), accountId.get());
        Property ownerIdProp = null;
        if (Optional.fromNullable(ownerId).isNone())
            ownerIdProp = Property.value("owner_id", Optional.some("The owner id."), "");
        else
            ownerIdProp = Property.value("owner_id", Optional.some("The owner id."), ownerId.get());
        Property parentIdProp = null;
        if (Optional.fromNullable(parentId).isNone())
            parentIdProp = Property.value("parent_id", Optional.some("The parent id."), "");
        else
            parentIdProp = Property.value("parent_id", Optional.some("The parent id."), parentId.get());
        Property groupIdProp = null;
        if (Optional.fromNullable(groupId).isNone())
            groupIdProp = Property.value("group_id", Optional.some("The group id."), "");
        else
            groupIdProp = Property.value("group_id", Optional.some("The group id."), groupId.get());
        Item.Builder itemBuilder = Item.builder(resourceUri);
        itemBuilder.addProperty(contentProp);
        itemBuilder.addProperty(accIdProp);
        itemBuilder.addProperty(ownerIdProp);
        itemBuilder.addProperty(parentIdProp);
        itemBuilder.addProperty(groupIdProp);
        Item postItem = itemBuilder.build();
        items.add(postItem);

        Template template = Template.create();

        Collection collection = Collection.create(resourceUri, links, items, queries, template, null);

        JSONObject postJson = new JSONObject(collection.toString());

        String url = ApplicationController.getApiUrl().toString() + "posts";
        final CustomJsonObjectRequest jsonRequest = new CustomJsonObjectRequest(Request.Method.POST, url, postJson, responseListener, errorListener);
        jsonRequest.setTag(tag);
        mRequestQueue.add(jsonRequest);

        //throw new UnsupportedOperationException("addPost() still need to be implemented.");
    }

}
