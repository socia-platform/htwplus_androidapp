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

    private SharedPreferencesController(Context context, String name, int mode) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(name, mode);
        mSPEditor = mSharedPreferences.edit();
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
        mSPEditor.putString("clientId", "b2f88822-c765-4c40-b8d8-60df634b745d");
        //mSPEditor.putString("apiUrl", "http://10.0.2.2:9000/api/");
        mSPEditor.putString("apiUrl", "http://192.168.0.212:9000/api/");
        mSPEditor.putLong("currentUserId", 49l);
        mSPEditor.commit();
    }

    public boolean hasAccessToken() {
        return (!mSharedPreferences.getString("accessToken", "").isEmpty());
    }

    public boolean hasAuthorizationToken() {
        return (!mSharedPreferences.getString("authorizationToken", "").isEmpty());
    }

    public String getAccessToken() {
        return mSharedPreferences.getString("accessToken", null);
    }

    public void setAccessToken(String token) {
        if (!token.isEmpty()) {
            mSPEditor.putString("accessToken", token);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Access token must be a valid String.");
    }

    public void removeAccessToken() {
        if (hasAccessToken()) {
            mSPEditor.remove("accessToken");
            mSPEditor.commit();
        }
    }

    public String getAuthorizationToken() {
        return mSharedPreferences.getString("authorizationToken", null);
    }

    public void setAuthorizationToken(String token) {
        if (!token.isEmpty()) {
            mSPEditor.putString("authorizationToken", token);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Authorization token must be a valid String.");
    }

    public void removeAuthorizationToken() {
        if (hasAuthorizationToken()) {
            mSPEditor.remove("authorizationToken");
            mSPEditor.commit();
        }
    }

    public String getClientId() {
        return mSharedPreferences.getString("clientId", null);
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

    public URL getAuthorizationUrl() {
        URL authUrl = null;
        if (!mSharedPreferences.getString("apiUrl", "").isEmpty() &&
                !mSharedPreferences.getString("clientId", "").isEmpty()) {
            try {
                authUrl = new URL(getApiUrl().toString() + "oauth2/authorize?clientId=" + getClientId());
            } catch (MalformedURLException muex) {
                Log.d("SharedPrefController", "Exception Occured: ", muex);
            }
        }
        return authUrl;
    }

    public String getPlaySessionValue() {
        return mSharedPreferences.getString("playSessionValue", null);
    }

    public void setPlaySessionValue(String sValue) {
        if (!sValue.isEmpty()) {
            mSPEditor.putString("playSessionValue", sValue);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("PlaySessionValue must be a valid String.");
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
        if (hasAuthorizationToken()) {
            mSPEditor.remove("currentUserId");
            mSPEditor.commit();
        }
    }
}
