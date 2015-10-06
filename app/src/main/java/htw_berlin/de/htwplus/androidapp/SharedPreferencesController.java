package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tino on 20.07.15.
 */
public class SharedPreferencesController {

    private static SharedPreferencesController mInstance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSPEditor;
    private static OAuth2Preferences oAuth2Pref;
    private static ApiRoutePreferences apiRoutePref;

    private SharedPreferencesController(Context context, String name, int mode) {
        mSharedPreferences = context.getSharedPreferences(name, mode);
        mSPEditor = mSharedPreferences.edit();
        oAuth2Pref = OAuth2Preferences.getInstance(context);
        apiRoutePref = ApiRoutePreferences.getInstance(context);
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

    public OAuth2Preferences oAuth2() {
        return oAuth2Pref;
    }

    public ApiRoutePreferences apiRoute() {
        return apiRoutePref;
    }
}
