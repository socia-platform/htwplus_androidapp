package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ApiRoutePreferences {

    private final static String ONE_USER_RESOURCE = "users/";
    private final static String ALL_USER_RESOURCES = "users";
    private final static String ONE_POST_RESOURCE = "posts/";
    private final static String ALL_POST_RESOURCES = "posts";
    private final static String OAUTH2_AUTHORIZE = "oauth2/authorize";
    private final static String OAUTH2_TOKEN = "oauth2/token";
    private final static String OAUTH2_CODE = "oauth2/code";
    private final static String PROTOCOL_CHARSET = "utf-8";

    private static ApiRoutePreferences mInstance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSPEditor;

    private ApiRoutePreferences(Context context) {
        mSharedPreferences =
                context.getSharedPreferences("ApiRoutePreferences", Context.MODE_PRIVATE);
        mSPEditor = mSharedPreferences.edit();
    }

    public static synchronized ApiRoutePreferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiRoutePreferences(context);
        }
        return mInstance;
    }

    public static synchronized ApiRoutePreferences getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(SharedPreferencesController.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        return mInstance;
    }

    public boolean hasApiUrl() {
        return (!mSharedPreferences.getString("apiUrl", "").isEmpty());
    }

    public URL getApiUrl() {
        URL apiUrl = null;
        try {
            apiUrl = new URL(mSharedPreferences.getString("apiUrl", null));
        } catch (MalformedURLException muex) {
            Log.d("ApiRoutePreferences", "Exception Occured: ", muex);
        }
        return apiUrl;
    }

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

    public String authorize(Map<String, String> params) {
        return String.format("%s%s%s", getApiUrl(), OAUTH2_AUTHORIZE, buildParamString(params));
    }

    public String code(Map<String, String> params) {
        return String.format("%s%s%s", getApiUrl(), OAUTH2_CODE, buildParamString(params));
    }

    public String token(Map<String, String> params) {
        return String.format("%s%s%s", getApiUrl(), OAUTH2_TOKEN, buildParamString(params));
    }

    public String user(long id, Map<String, String> params) {
        return String.format("%s%s%d%s", getApiUrl(), ONE_USER_RESOURCE,
                id, buildParamString(params));
    }

    public String users(Map<String, String> params) {
        return String.format("%s%s%s", getApiUrl(), ALL_USER_RESOURCES, buildParamString(params));
    }

    public String post(long id, Map<String, String> params) {
        return String.format("%s%s%d%s", getApiUrl(), ONE_POST_RESOURCE,
                id, buildParamString(params));
    }

    public String posts(Map<String, String> params) {
        return String.format("%s%s%s", getApiUrl(), ALL_POST_RESOURCES, buildParamString(params));
    }

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
