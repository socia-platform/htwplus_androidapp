package htw_berlin.de.htwplus.androidapp;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import net.hamnaberg.json.Collection;
import net.hamnaberg.json.parser.CollectionParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import htw_berlin.de.htwplus.androidapp.datamodel.ApiError;
import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;
import htw_berlin.de.htwplus.androidapp.util.JsonCollectionHelper;

/**
 * Created by tino on 04.10.15.
 */
public class CollectionJsonRequest<T> extends Request<T> {
    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";
    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/vnd.collection+json; charset=%s", PROTOCOL_CHARSET);
    private final static CollectionParser collectionParser = new CollectionParser();
    private final Response.Listener<T> listener;
    private final Map<String, String> customHeaders;
    private final Class<T> clazz;
    private Collection body;

    public CollectionJsonRequest(int method, String url, Class<T> clazz,
                                 Map<String, String> customHeaders,
                                 Collection body, Response.Listener<T> listener,
                                 Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        boolean isOk = ((url != null) && (!url.isEmpty()) && (customHeaders != null)
                        && (listener != null) && (errorListener != null)
                        && (((body != null) && (Method.POST == method))
                        || ((body == null) && (Method.GET == method))));

        if (isOk) {
            this.listener = listener;
            this.customHeaders = customHeaders;
            this.clazz = clazz;
            this.body = body;
        } else
            throw new IllegalArgumentException("Invalid arguments.");
    }

    public CollectionJsonRequest(int method, String url, Class<T> clazz, Collection body,
                                 Response.Listener<T> listener,
                                 Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        boolean isOk = ((url != null) && (!url.isEmpty())
                        && (errorListener != null) && (listener != null)
                        && (((body != null) && (Method.POST == method))
                        || ((body == null) && (Method.GET == method))));
        if (isOk) {
            this.listener = listener;
            this.customHeaders = new HashMap<String, String>();
            this.clazz = clazz;
            this.body = body;
        } else
            throw new IllegalArgumentException("Invalid arguments.");
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        Response returnResponse = null;
        try {
            if (response.data.length > 0) {
                String rawJsonCollection =
                        new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                Collection collectionJson = parseToCollection(rawJsonCollection);
                if (!JsonCollectionHelper.hasError(collectionJson)) {
                    Object parseResult = null;
                    if (clazz.equals(User.class))
                        parseResult = parseToAccounts(collectionJson);
                    else if (clazz.equals(Post.class))
                        parseResult = parseToPosts(collectionJson);
                    else {
                        String ms = "Expected return class type is not supported";
                        Response.error(new ParseError(new UnsupportedOperationException(ms)));
                    }
                    returnResponse =
                            Response.success(parseResult, HttpHeaderParser.parseCacheHeaders(response));
                } else {
                    ApiError apiError = parseToApiError(collectionJson);
                    Throwable customThrow = new Throwable(apiError.getMessage());
                    returnResponse = Response.error(new ParseError(customThrow));
                }
            } else if ((response.data.length == 0) && (response.statusCode == 200)) {
                returnResponse =
                        Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            returnResponse = Response.error(new ParseError(e));
        } catch (IOException e) {
            returnResponse = Response.error(new ParseError(e));
        }
        return returnResponse;
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        customHeaders.put("Accept", PROTOCOL_CONTENT_TYPE);
        customHeaders.put("Content-Type", PROTOCOL_CONTENT_TYPE);
        return customHeaders;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        byte[] returnBody = null;
        try {
            if (body != null)
                returnBody = body.toString().getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    body.toString(), PROTOCOL_CHARSET);
        }
        return returnBody;
    }

    private Collection parseToCollection(String rawJsonCollection) throws IOException {
        return collectionParser.parse(rawJsonCollection);
    }

    private List<User> parseToAccounts(Collection collectionJson) {
        return JsonCollectionHelper.toUsers(collectionJson);
    }

    private List<Post> parseToPosts(Collection collectionJson) {
        return JsonCollectionHelper.toPosts(collectionJson);
    }

    private ApiError parseToApiError(Collection collectionJson) {
        return JsonCollectionHelper.toError(collectionJson);
    }
}
