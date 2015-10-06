package htw_berlin.de.htwplus.androidapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesController {

    private static SharedPreferencesController mInstance;
    private SharedPreferences mSharedPreferences;
    private static OAuth2Preferences mOAuth2Pref;
    private static ApiRoutePreferences mApiRoutePref;

    private SharedPreferencesController(Context context, String name, int mode) {
        mSharedPreferences = context.getSharedPreferences(name, mode);
        mOAuth2Pref = OAuth2Preferences.getInstance(context);
        mApiRoutePref = ApiRoutePreferences.getInstance(context);
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
        return mOAuth2Pref;
    }

    public ApiRoutePreferences apiRoute() {
        return mApiRoutePref;
    }
}
