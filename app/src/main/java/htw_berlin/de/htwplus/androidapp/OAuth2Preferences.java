package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Represents a wrapper about all attributes / preferences respective OAuth2 for HTWPLUS
 * RESTful API.<br />
 * <br />
 * This is a singleton class and should use only in combination with application singleton.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class OAuth2Preferences {

    /** Static instance of this class. */
    private static OAuth2Preferences mInstance;

    /** Shared preferences object, which is required to address attributes. */
    private static SharedPreferences mSharedPreferences;

    /** Editor object, which is required to edit attributes. */
    private static SharedPreferences.Editor mSPEditor;

    /**
     * Creates a new preference wrapper for OAuth2 attributes with the given context.<br />
     * The preferences are internal addressable through <i>OAuth2Preferences</i>.
     *
     * @param context Context of preferences
     */
    private OAuth2Preferences(Context context) {
        mSharedPreferences =
                context.getSharedPreferences("OAuth2Preferences", Context.MODE_PRIVATE);
        mSPEditor = mSharedPreferences.edit();
        setInitialPreferences();
    }

    /**
     * Returns the unique instance of this class, if it not instantiate yet so a new instance
     * will be created with the given context.
     *
     * @param context Context of preferences
     *
     * @return Unique instance of this class.
     */
    public static synchronized OAuth2Preferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new OAuth2Preferences(context);
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
    public static synchronized OAuth2Preferences getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(OAuth2Preferences.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        return mInstance;
    }

    /**
     * Sets initial attributes, which must be set for useful functional capability.
     */
    private void setInitialPreferences() {
        mSPEditor.putString("clientId", "");
        mSPEditor.putString("clientSecret", "");
        mSPEditor.putString("authCallBackURI", "");
        mSPEditor.commit();
    }

    /**
     * Checks if an access token exists.
     *
     * @return True if an access token exists otherwise false.
     */
    public boolean hasAccessToken() {
        return (!mSharedPreferences.getString("accessToken", "").isEmpty());
    }

    /**
     * Returns the access token.
     *
     * @return Access token or null if it not exists.
     */
    public String getAccessToken() {
        return mSharedPreferences.getString("accessToken", null);
    }

    /**
     * Sets an access token.<br /><br />
     * If an access token already exists, it is overwritten.
     *
     * @param token Access token to be set.
     */
    public void setAccessToken(String token) {
        if (!token.isEmpty()) {
            mSPEditor.putString("accessToken", token);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Access token must be a valid String.");
    }

    /**
     * Removes the access token.
     */
    public void removeAccessToken() {
        if (hasAccessToken()) {
            mSPEditor.remove("accessToken");
            mSPEditor.commit();
        }
    }

    /**
     * Checks if an authorization token exists.
     *
     * @return True if an authorization token exists otherwise false.
     */
    public boolean hasAuthorizationToken() {
        return (!mSharedPreferences.getString("authorizationToken", "").isEmpty());
    }

    /**
     * Returns the authorization token.
     *
     * @return Authorization token or null if it not exists.
     */
    public String getAuthorizationToken() {
        return mSharedPreferences.getString("authorizationToken", null);
    }

    /**
     * Sets an authorization token.<br /><br />
     * If an authorization token already exists, it is overwritten.
     *
     * @param token Authorization token to be set.
     */
    public void setAuthorizationToken(String token) {
        if (!token.isEmpty()) {
            mSPEditor.putString("authorizationToken", token);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Authorization token must be a valid String.");
    }

    /**
     * Removes the authorization token.
     */
    public void removeAuthorizationToken() {
        if (hasAuthorizationToken()) {
            mSPEditor.remove("authorizationToken");
            mSPEditor.commit();
        }
    }

    /**
     * Checks if a refresh token exists.
     *
     * @return True if a refresh token exists otherwise false.
     */
    public boolean hasRefreshToken() {
        return (!mSharedPreferences.getString("refreshToken", "").isEmpty());
    }

    /**
     * Returns the refresh token.
     *
     * @return Refresh token or null if it not exists.
     */
    public String getRefreshToken() {
        return mSharedPreferences.getString("refreshToken", null);
    }

    /**
     * Sets an refresh token.<br /><br />
     * If a refresh token already exists, it is overwritten.
     *
     * @param refreshToken Refresh token to be set.
     */
    public void setRefreshToken(String refreshToken) {
        if (!refreshToken.isEmpty()) {
            mSPEditor.putString("refreshToken", refreshToken);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Refresh token must be a valid String.");
    }

    /**
     * Removes the refresh token.
     */
    public void removeRefreshToken() {
        if (hasRefreshToken()) {
            mSPEditor.remove("refreshToken");
            mSPEditor.commit();
        }
    }

    /**
     * Checks if an expire time for access token exists.
     *
     * @return True if an expire time exists otherwise false.
     */
    public boolean hasExpiredTimeAccessToken() {
        return (mSharedPreferences.getLong("expiredTimeAccessToken", -1) != -1);
    }

    /**
     * Returns the expire time for access token.<br /><br/>
     * The expire time is set as a timestamp, which (should) is in the future.
     *
     * @return Expire time for access token.
     */
    public Date getExpiredTimeAccessToken() {
        Date expiredDate = null;
        long expSeconds = mSharedPreferences.getLong("expiredTimeAccessToken", -1);
        if (expSeconds != -1)
            expiredDate = new Date(expSeconds * 1000);
        return expiredDate;
    }

    /**
     * Sets the expire time (best before date) for access token.
     *
     * @param expiredDate Best before date
     *
     * @throws IllegalArgumentException Throws if the given best before date is null or not in the
     * future.
     */
    public void setExpiredTimeAccessToken(Date expiredDate) {
        if ((expiredDate != null) && (expiredDate.compareTo(new Date()) > 0)) {
            mSPEditor.putLong("expiredTimeAccessToken", (expiredDate.getTime() / 1000l));
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Elapsed Date must be a valid Date and must be "
                    + "after current date.");
    }

    /**
     * Removes the expire time for access token.
     */
    public void removeExpiredTimeAccessToken() {
        if (hasRefreshToken()) {
            mSPEditor.remove("expiredTimeAccessToken");
            mSPEditor.commit();
        }
    }

    /**
     * Checks if access token is expired.
     *
     * @return True if access token is expired otherwise false.
     */
    public boolean isAccessTokenExpired() {
        boolean isExpired = true;
        if (hasAccessToken() && hasExpiredTimeAccessToken()) {
            Date expDate = getExpiredTimeAccessToken();
            isExpired = expDate.before(new Date());
        }
        return isExpired;
    }

    /**
     * Returns the OAuth2 client id.
     *
     * @return Id of OAuth2 client.
     */
    public String getClientId() {
        return mSharedPreferences.getString("clientId", null);
    }

    /**
     * Returns the client secret.
     *
     * @return The client secret.
     */
    public String getClientSecret() {
        return mSharedPreferences.getString("clientSecret", null);
    }

    /**
     * Returns the authentication callback uri.
     *
     * @return The authentication callback uri.
     */
    public String getAuthCallBackURI() {
        return mSharedPreferences.getString("authCallBackURI", null);
    }

    /**
     * Checks if id of the user, who is current logged in, exists.
     *
     * @return True if user id exists otherwise false.
     */
    public boolean hasCurrentUserId() {
        return (mSharedPreferences.getLong("currentUserId", -1) != -1);
    }

    /**
     * Sets the id of user, who is current logged in.
     *
     * @param userId Id of user, who is current logged in
     *
     * @throws IllegalArgumentException Throws if the given id is less than 1.
     */
    public void setCurrentUserId(long userId) {
        if (userId > 0) {
            mSPEditor.putLong("currentUserId", userId);
            mSPEditor.commit();
        } else
            throw new IllegalArgumentException("Current user id must be a greater than 0.");
    }

    /**
     * Returns the id of user, who is current logged in.
     *
     * @return The id of user, who is current logged in.
     */
    public long getCurrentUserId() {
        return mSharedPreferences.getLong("currentUserId", -1);
    }

    /**
     * Removes the id of user, who is current logged in.
     */
    public void removeCurrentUserId() {
        if (hasAuthorizationToken()) {
            mSPEditor.remove("currentUserId");
            mSPEditor.commit();
        }
    }
}
