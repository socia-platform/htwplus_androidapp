package htw_berlin.de.htwplus.androidapp;

/**
 * Represents a controller, which manage the main functionality of the app.<br />
 * <br />
 * This singleton class should to be used to access of all functionality.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class Application extends android.app.Application {

    /** Static instance of this class. */
    private static Application mInstance;

    /** Instance of volley http controller. */
    private static VolleyNetworkController mVncInstance;

    /** Instance of shared preference proxy. */
    private static SharedPreferencesProxy mSpcInstance;

    /**
     * Called after app start and instantiates volley http controller and shared preference proxy.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mVncInstance = VolleyNetworkController.getInstance(getApplicationContext());
        mSpcInstance = SharedPreferencesProxy.getInstance(getApplicationContext());
    }

    /**
     * Returns the unique instance of this class.
     *
     * @return Unique instance of this class.
     *
     * @throws IllegalStateException Throws if the instance is not instantiate yet.
     */
    public static synchronized Application getInstance() {
        return mInstance;
    }

    /**
     * Returns the unique instance of volley http controller class.
     *
     * @return Unique instance of volley http controller class
     */
    public static VolleyNetworkController network() {
        return mVncInstance;
    }

    /**
     * Returns the unique instance of shared preference proxy class.
     *
     * @return Unique instance of shared preference proxy class
     */
    public static SharedPreferencesProxy preferences() {
        return mSpcInstance;
    }

    /**
     * Checks if an api url and an access token exists.
     *
     * @return True if both exists otherwise false.
     */
    public static boolean isWorkingState() {
        boolean isWorking = (preferences().apiRoute().hasApiUrl() &&
                             !preferences().oAuth2().isAccessTokenExpired());
        return isWorking;
    }
}
