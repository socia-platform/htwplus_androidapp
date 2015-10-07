package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Represents a wrapper about all attributes / preferences respective url routes for HTWPLUS
 * RESTful API. <br />
 * <br />
 * This is a singleton class and should use only in combination with application singleton.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class ApiRoutePreferences {

    /** URL segment to address single user resource. */
    private final static String ONE_USER_RESOURCE = "users/";

    /** URL segment to address all user resources. */
    private final static String ALL_USER_RESOURCES = "users";

    /** URL segment to address single posting resource. */
    private final static String ONE_POST_RESOURCE = "posts/";

    /** URL segment to address all posting resources. */
    private final static String ALL_POST_RESOURCES = "posts";

    /** URL segment to address a authorization token. */
    private final static String OAUTH2_AUTHORIZE = "oauth2/authorize";

    /** URL segment to address a access token. */
    private final static String OAUTH2_TOKEN = "oauth2/token";

    /** Charset of the protocol. */
    private final static String PROTOCOL_CHARSET = "utf-8";

    /** Static instance of this class. */
    private static ApiRoutePreferences mInstance;

    /** Shared preferences object, which is required to address attributes. */
    private SharedPreferences mSharedPreferences;

    /** Editor object, which is required to edit attributes. */
    private SharedPreferences.Editor mSPEditor;

    /**
     * Creates a new preference wrapper for api url route attributes with the given context.<br />
     * The preferences are internal addressable through <i>ApiRoutePreferences</i>.
     *
     * @param context Context of preferences
     */
    private ApiRoutePreferences(Context context) {
        mSharedPreferences =
                context.getSharedPreferences("ApiRoutePreferences", Context.MODE_PRIVATE);
        mSPEditor = mSharedPreferences.edit();
    }

    /**
     * Returns the unique instance of this class, if it not instantiate yet so a new instance
     * will be created with the given context.
     *
     * @param context Context of preferences
     *
     * @return Unique instance of this class.
     */
    public static synchronized ApiRoutePreferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiRoutePreferences(context);
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
    public static synchronized ApiRoutePreferences getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(SharedPreferencesProxy.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        return mInstance;
    }

    /**
     * Checks if a api url exists.
     *
     * @return True if api url exists otherwise false.
     */
    public boolean hasApiUrl() {
        return (!mSharedPreferences.getString("apiUrl", "").isEmpty());
    }

    /**
     * Returns the api url.
     *
     * @return URL to the api.
     */
    public URL getApiUrl() {
        URL apiUrl = null;
        try {
            apiUrl = new URL(mSharedPreferences.getString("apiUrl", null));
        } catch (MalformedURLException muex) {
            Log.d("ApiRoutePreferences", "Exception Occured: ", muex);
        }
        return apiUrl;
    }

    /**
     * Sets the api url.<br /><br />
     * If a url already exists, it is overwritten.
     *
     * @param apiUrl API URL to be set
     *
     * @return Set api url.
     */
    public URL setApiUrl(URL apiUrl) {
        if (apiUrl != null) {
            String apiUrlString = apiUrl.toString();
            if (!apiUrlString.endsWith("/"))
                apiUrlString += "/";
            mSPEditor.putString("apiUrl", apiUrlString);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Api url may not be null.");
        return apiUrl;
    }

    /**
     * Returns the URL to address a authorization token.
     *
     * @param params Query params, which should add to url
     *
     * @return URL to address a authorization token.
     */
    public String authorize(Map<String, String> params) {
        return String.format("%s%s%s", getApiUrl(), OAUTH2_AUTHORIZE, buildParamString(params));
    }

    /**
     * Returns the URL to address a access token.
     *
     * @param params Query params, which should add to url
     *
     * @return URL to address a access token.
     */
    public String token(Map<String, String> params) {
        return String.format("%s%s%s", getApiUrl(), OAUTH2_TOKEN, buildParamString(params));
    }

    /**
     * Returns the URL to address a single user resource.
     *
     * @param id Id of the addressed user resource
     * @param params Query params, which should add to url
     *
     * @return URL to address a single user resource.
     */
    public String user(long id, Map<String, String> params) {
        return String.format("%s%s%d%s", getApiUrl(), ONE_USER_RESOURCE,
                id, buildParamString(params));
    }

    /**
     * Returns the URL to address all user resources.
     *
     * @param params Query params, which should add to url
     *
     * @return URL to address all user resources.
     */
    public String users(Map<String, String> params) {
        return String.format("%s%s%s", getApiUrl(), ALL_USER_RESOURCES, buildParamString(params));
    }

    /**
     * Returns the URL to address a single posting resource.
     *
     * @param id Id of the addressed posting resource
     * @param params Query params, which should add to url
     *
     * @return URL to address a single posting resource.
     */
    public String post(long id, Map<String, String> params) {
        return String.format("%s%s%d%s", getApiUrl(), ONE_POST_RESOURCE,
                id, buildParamString(params));
    }

    /**
     * Returns the URL to address all posting resources.
     *
     * @param params Query params, which should add to url
     *
     * @return URL to address all posting resources.
     */
    public String posts(Map<String, String> params) {
        return String.format("%s%s%s", getApiUrl(), ALL_POST_RESOURCES, buildParamString(params));
    }

    /**
     * Builds a query of url with the given param map.
     *
     * @param params Query params to be assemble
     *
     * @return Assembled url query.
     */
    private String buildParamString(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet())
        {
            try {
                if (builder.toString().contains("?"))
                    builder.append('&');
                else
                    builder.append('?');
                builder.append(URLEncoder.encode(param.getKey(), PROTOCOL_CHARSET));
                builder.append('=');
                builder.append(URLEncoder.encode(param.getValue(), PROTOCOL_CHARSET));
            } catch (UnsupportedEncodingException e) {
                Log.d("ApiRoutePreferences", "Exception Occured: ", e);
            }
        }
        return builder.toString();
    }
}
