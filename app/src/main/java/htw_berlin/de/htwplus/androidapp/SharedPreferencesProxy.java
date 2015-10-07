package htw_berlin.de.htwplus.androidapp;

import android.content.Context;

/**
 * Represents a proxy, which manage all preference wrapper instances.<br />
 * <br />
 * This is a singleton class and should use only in combination with application singleton.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class SharedPreferencesProxy {

    /** Static instance of this class. */
    private static SharedPreferencesProxy mInstance;

    /** Instance of OAuth2 preference wrapper. */
    private static OAuth2Preferences mOAuth2Pref;

    /** Instance of api route preference wrapper. */
    private static ApiRoutePreferences mApiRoutePref;

    /**
     * Creates a new preference proxy with the given context.
     *
     * @param context Context of proxy
     */
    private SharedPreferencesProxy(Context context) {
        mOAuth2Pref = OAuth2Preferences.getInstance(context);
        mApiRoutePref = ApiRoutePreferences.getInstance(context);
    }

    /**
     * Returns the unique instance of this class, if it not instantiate yet so a new instance
     * will be created with the given context.
     *
     * @param context Context of preferences
     *
     * @return Unique instance of this class.
     */
    public static synchronized SharedPreferencesProxy getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesProxy(context);
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
    public static synchronized SharedPreferencesProxy getInstance() {
        if (mInstance == null)
            throw new IllegalStateException(SharedPreferencesProxy.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        return mInstance;
    }

    /**
     * Returns the unique instance of OAuth2 preference wrapper class.
     *
     * @return Unique instance of OAuth2 preference wrapper class
     */
    public OAuth2Preferences oAuth2() {
        return mOAuth2Pref;
    }

    /**
     * Returns the unique instance of api route preference wrapper class.
     *
     * @return Unique instance of api route preference wrapper class
     */
    public ApiRoutePreferences apiRoute() {
        return mApiRoutePref;
    }
}
