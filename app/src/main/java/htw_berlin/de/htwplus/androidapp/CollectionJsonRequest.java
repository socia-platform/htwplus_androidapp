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
 * A request for retrieving a Collection+JSON response body at a given URL, allowing for an
 * optional Collection+JSON object to be passed in as part of the request body.<br /><br />
 *
 * The responded body (if is a GET request) will be converted to the type, which corresponds to the
 * given class parameter. Supported class types are <i>net.hamnaberg.json.Collection</i>,
 * <i>htw_berlin.de.htwplus.androidapp.datamodel.Post</i> and <i>htw_berlin.de.htwplus.androidapp
 * .datamodel.User</i>.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class CollectionJsonRequest<T> extends Request<T> {

    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/vnd.collection+json; charset=%s", PROTOCOL_CHARSET);

    /** Parser to parse the Collection+JSON data in responded body. */
    private final static CollectionParser mCollectionParser = new CollectionParser();

    /** Response listener to delivery the parsed response to callback method. */
    private final Response.Listener<T> mListener;

    /** Map with custom header params. */
    private final Map<String, String> mCustomHeaders;

    /** Class type to be converted. */
    private final Class<T> mClazz;

    /** Body content in form of parsed Collection+JSON data. */
    private Collection mBodyContent;

    /**
     * Creates and sends a http Collection+JSON request with the given method, url, class, custom
     * headers, body, response listener and error listener.<br /><br />
     *
     * Supported kinds of http requests are GET and POST. Should send a GET request the body must
     * be null or must be set if should be a POST request.
     *
     * @param method Request method
     * @param url Request url
     * @param clazz Class type to be converted of responded body content
     * @param customHeaders Custom headers
     * @param body Body content to sent if is a post request
     * @param listener Response listener to be set for callback
     * @param errorListener Error listener to be set for callback
     *
     * @throws IllegalArgumentException Throws if url is null or empty, custom headers is null,
     * response listener is null, error listener is null, body is null if the given method is
     * POST or body is not null if the given method is GET.
     */
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
            this.mListener = listener;
            this.mCustomHeaders = customHeaders;
            this.mClazz = clazz;
            this.mBodyContent = body;
        } else
            throw new IllegalArgumentException("Invalid arguments.");
    }

    /**
     * Creates and sends a http Collection+JSON request with the given method, url, class, body,
     * response listener and error listener.<br /><br />
     *
     * Supported kinds of http requests are GET and POST. Should send a GET request the body must
     * be null or must be set if should be a POST request.
     *
     * @param method Request method
     * @param url Request url
     * @param clazz Class type to be converted of responded body content
     * @param body Body content to sent if is a post request
     * @param listener Response listener to be set for callback
     * @param errorListener Error listener to be set for callback
     *
     * @throws IllegalArgumentException Throws if url is null or empty, response listener is
     * null, error listener is null, body is null if the given method is
     * POST or body is not null if the given method is GET.
     */
    public CollectionJsonRequest(int method, String url, Class<T> clazz, Collection body,
                                 Response.Listener<T> listener,
                                 Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        boolean isOk = ((url != null) && (!url.isEmpty())
                        && (errorListener != null) && (listener != null)
                        && (((body != null) && (Method.POST == method))
                        || ((body == null) && (Method.GET == method))));
        if (isOk) {
            this.mListener = listener;
            this.mCustomHeaders = new HashMap<String, String>();
            this.mClazz = clazz;
            this.mBodyContent = body;
        } else
            throw new IllegalArgumentException("Invalid arguments.");
    }

    /**
     * Called after network response is successful parsed.<br />
     * Registration the listener for response callback.
     *
     * @param response Parsed network response
     */
    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    /**
     * Called if a network response has occurred. <br /><br />
     *
     * This method parses and converts the body of the network response (if present) and decided if
     * is a expected response.
     *
     * @param response Contains the response payload as a byte[], HTTP status code, and response
     *                 headers
     *
     * @return Parsed and converted network response or null if an unexpected response is reached.
     */
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
                    if (mClazz.equals(User.class))
                        parseResult = parseToAccounts(collectionJson);
                    else if (mClazz.equals(Post.class))
                        parseResult = parseToPosts(collectionJson);
                    else {
                        String ms = "Expected return class type is not supported";
                        Response.error(new ParseError(new UnsupportedOperationException(ms)));
                    }
                    returnResponse = Response.success(parseResult,
                            HttpHeaderParser.parseCacheHeaders(response));
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

    /**
     * Returns the custom headers of the http request.
     *
     * @return Custom headers of the http request.
     *
     * @throws AuthFailureError
     */
    @Override
    public Map getHeaders() throws AuthFailureError {
        mCustomHeaders.put("Accept", PROTOCOL_CONTENT_TYPE);
        mCustomHeaders.put("Content-Type", PROTOCOL_CONTENT_TYPE);
        return mCustomHeaders;
    }

    /**
     * Returns the body content type of the http request.
     *
     * @return Body content type of the http request.
     */
    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    /**
     * Returns the body content data of the http request
     *
     * @return Body content data of the http request.
     */
    @Override
    public byte[] getBody() {
        byte[] returnBody = null;
        try {
            if (mBodyContent != null)
                returnBody = mBodyContent.toString().getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mBodyContent.toString(), PROTOCOL_CHARSET);
        }
        return returnBody;
    }

    /**
     * Parses the given Collection+JSON string to an object.
     *
     * @param rawJsonCollection Collection+JSON string to be parsed
     *
     * @return Parsed Collection+JSON data as object.
     *
     * @throws IOException Throws if an error occurred during the parsing.
     */
    private Collection parseToCollection(String rawJsonCollection) throws IOException {
        return mCollectionParser.parse(rawJsonCollection);
    }

    /**
     * Parses the given Collection+JSON for user data and converts them to a list of user-objects.
     *
     * @param collectionJson Collection+JSON to be parsed.
     *
     * @return List of parsed and converted user-objects.
     */
    private List<User> parseToAccounts(Collection collectionJson) {
        return JsonCollectionHelper.toUsers(collectionJson);
    }

    /**
     * Parses the given Collection+JSON for posting data and converts them to a list of
     * posting-objects
     *
     * @param collectionJson Collection+JSON to be parsed.
     * @return List of parsed and converted posting-objects.
     */
    private List<Post> parseToPosts(Collection collectionJson) {
        return JsonCollectionHelper.toPosts(collectionJson);
    }

    /**
     * Parses the given Collection+JSON for error data and converts them to a api error object.
     *
     * @param collectionJson Collection+JSON to be parsed.
     * @return Parsed and converted Api error object.
     */
    private ApiError parseToApiError(Collection collectionJson) {
        return JsonCollectionHelper.toError(collectionJson);
    }
}
