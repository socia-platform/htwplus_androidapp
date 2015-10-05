package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tino on 20.07.15.
 */
public class SharedPreferencesController {

    private static SharedPreferencesController mInstance;
    private static Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSPEditor;
    private static OAuth2Preferences oAuth2Pref;

    private SharedPreferencesController(Context context, String name, int mode) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(name, mode);
        mSPEditor = mSharedPreferences.edit();
        oAuth2Pref = OAuth2Preferences.getInstance(context);
        setInitialPreferences();
    }

    public static synchronized SharedPreferencesController getInstance(Context context,
                                                                       String name,
                                                                       int mode) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesController(context, name, mode);
        }
        return mInstance;
    }

    public static synchronized SharedPreferencesController getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(SharedPreferencesController.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        return mInstance;
    }

    private void setInitialPreferences() {
        mSPEditor.putLong("currentUserId", 49l);
        mSPEditor.commit();
    }

    public OAuth2Preferences oAuth2() {
        return oAuth2Pref;
    }

    public boolean hasApiUrl() {
        return (!mSharedPreferences.getString("apiUrl", "").isEmpty());
    }

    public URL getApiUrl() {
        URL apiUrl = null;
        try {
            apiUrl = new URL(mSharedPreferences.getString("apiUrl", null));
        } catch (MalformedURLException muex) {
            Log.d("SharedPrefController", "Exception Occured: ", muex);
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

    public URL getAuthorizationUrl() {
        URL authUrl = null;
        if (!mSharedPreferences.getString("apiUrl", "").isEmpty() &&
                !mSharedPreferences.getString("clientId", "").isEmpty()) {
            try {
                String addition = "oauth2/authorize?client_id=" + oAuth2Pref.getClientId();
                addition += "&client_secret=" + oAuth2Pref.getClientSecret();
                addition += "&response_type=code&redirect_uri=" + oAuth2Pref.getAuthCallBackURI();
                authUrl = new URL(getApiUrl().toString() + addition);
            } catch (MalformedURLException muex) {
                Log.d("SharedPrefController", "Exception Occured: ", muex);
            }
        }
        return authUrl;
    }

    public void setCurrentUserId(long userId) {
        if (userId > -1) {
            mSPEditor.putLong("currentUserId", userId);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("PlaySessionValue must be a valid String.");
    }

    public long getCurrentUserId() {
        return mSharedPreferences.getLong("currentUserId", -1);
    }

    public void removeCurrentUserId() {
        if (oAuth2Pref.hasAuthorizationToken()) {
            mSPEditor.remove("currentUserId");
            mSPEditor.commit();
        }
    }
}
