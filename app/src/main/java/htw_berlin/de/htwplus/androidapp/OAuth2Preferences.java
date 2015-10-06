package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class OAuth2Preferences {

    private static OAuth2Preferences mInstance;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mSPEditor;

    private OAuth2Preferences(Context context) {
        mSharedPreferences =
                context.getSharedPreferences("OAuth2Preferences", Context.MODE_PRIVATE);
        mSPEditor = mSharedPreferences.edit();
        setInitialPreferences();
    }

    public static synchronized OAuth2Preferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new OAuth2Preferences(context);
        }
        return mInstance;
    }

    public static synchronized OAuth2Preferences getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(OAuth2Preferences.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        return mInstance;
    }

    private void setInitialPreferences() {
        mSPEditor.putString("clientId", "");
        mSPEditor.putString("clientSecret", "");
        mSPEditor.putString("authCallBackURI", "");
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
            throw new IllegalArgumentException("Elapsed Date must be a valid Date and must be "
                    + "after current date.");
    }

    public void removeExpiredTimeAccessToken() {
        if (hasRefreshToken()) {
            mSPEditor.remove("expiredTimeAccessToken");
            mSPEditor.commit();
        }
    }

    public boolean isAccessTokenExpired() {
        boolean isExpired = true;
        if (hasAccessToken() && hasExpiredTimeAccessToken()) {
            Date expDate = getExpiredTimeAccessToken();
            isExpired = expDate.before(new Date());
        }
        return isExpired;
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

    public boolean hasCurrentUserId() {
        return (mSharedPreferences.getLong("currentUserId", -1) != -1);
    }

    public void setCurrentUserId(long userId) {
        if (userId > 0) {
            mSPEditor.putLong("currentUserId", userId);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Current user id must be a greater than 0.");
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