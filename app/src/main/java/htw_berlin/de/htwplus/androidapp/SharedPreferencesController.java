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
    private static ApiRoutePreferences apiRoutePref;

    private SharedPreferencesController(Context context, String name, int mode) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(name, mode);
        mSPEditor = mSharedPreferences.edit();
        oAuth2Pref = OAuth2Preferences.getInstance(context);
        apiRoutePref = ApiRoutePreferences.getInstance(context);
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

    public ApiRoutePreferences apiRoute() {
        return apiRoutePref;
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
