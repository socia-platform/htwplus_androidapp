package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

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
        mSPEditor.putString("clientSecret", "fe538d15-46b1-412b-812d-9838f483aec3");
        mSPEditor.putString("authCallBackURI", "https://localhost/androidclient");
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

    public boolean hasRefreshToken() {
        return (!mSharedPreferences.getString("refreshToken", "").isEmpty());
    }

    public String getRefreshToken() {
        return mSharedPreferences.getString("refreshToken", null);
    }

    public void setRefreshToken(String refreshToken) {
        if (!refreshToken.isEmpty()) {
            mSPEditor.putString("refreshToken", refreshToken);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Refresh token must be a valid String.");
    }

    public void removeRefreshToken() {
        if (hasRefreshToken()) {
            mSPEditor.remove("refreshToken");
            mSPEditor.commit();
        }
    }

    public boolean hasExpiredTimeAccessToken() {
        return (mSharedPreferences.getLong("expiredTimeAccessToken", -1) != -1);
    }

    public Date getExpiredTimeAccessToken() {
        Date expiredDate = null;
        long expSeconds = mSharedPreferences.getLong("expiredTimeAccessToken", -1);
        if (expSeconds != -1)
            expiredDate = new Date(expSeconds * 1000);
        return expiredDate;
    }

    public void setExpiredTimeAccessToken(Date expiredDate) {
        if ((expiredDate != null) && (expiredDate.compareTo(new Date()) > 0)) {
            mSPEditor.putLong("expiredTimeAccessToken", (expiredDate.getTime() / 1000l));
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Elapsed Date must be a valid Date and must be " +
                    "after current date.");
    }

    public void removeExpiredTimeAccessToken() {
        if (hasRefreshToken()) {
            mSPEditor.remove("expiredTimeAccessToken");
            mSPEditor.commit();
        }
    }


    public String getClientId() {
        return mSharedPreferences.getString("clientId", null);
    }

    public String getClientSecret() {
        return mSharedPreferences.getString("clientSecret", null);
    }

    public String getAuthCallBackURI() {
        return mSharedPreferences.getString("authCallBackURI", null);
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
                String addition = "oauth2/authorize?client_id=" + getClientId();
                addition += "&client_secret=" + getClientSecret();
                addition += "&response_type=code&redirect_uri=" + getAuthCallBackURI();
                authUrl = new URL(getApiUrl().toString() + addition);
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
