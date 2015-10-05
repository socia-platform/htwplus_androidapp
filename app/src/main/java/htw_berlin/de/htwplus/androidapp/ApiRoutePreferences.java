package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tino on 05.10.15.
 */
public class ApiRoutePreferences {
    private static ApiRoutePreferences mInstance;
    private static Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSPEditor;

    private ApiRoutePreferences(Context context) {
        mContext = context;
        mSharedPreferences =
                context.getSharedPreferences("ApiRoutePreferences", Context.MODE_PRIVATE);
        mSPEditor = mSharedPreferences.edit();
        setInitialPreferences();
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

    private void setInitialPreferences() {

        mSPEditor.commit();
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

    public void removeApiUrl() {
        if (hasApiUrl()) {
            mSPEditor.remove("apiUrl");
            mSPEditor.commit();
        }
    }
}
