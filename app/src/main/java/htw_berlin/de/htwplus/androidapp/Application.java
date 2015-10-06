package htw_berlin.de.htwplus.androidapp;

public class Application extends android.app.Application {

    private static Application mInstance;
    private static VolleyNetworkController mVncInstance;
    private static SharedPreferencesController mSpcInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mVncInstance = VolleyNetworkController.getInstance(getApplicationContext());
        mSpcInstance = SharedPreferencesController.getInstance(getApplicationContext(),
                                                                "AppPreferences",
                                                                MODE_PRIVATE);
    }

    public static synchronized Application getInstance() {
        return mInstance;
    }

    public static VolleyNetworkController network() {
        return mVncInstance;
    }

    public static SharedPreferencesController preferences() {
        return mSpcInstance;
    }

    public static boolean isWorkingState() {
        boolean isWorking = (preferences().apiRoute().hasApiUrl() &&
                             !preferences().oAuth2().isAccessTokenExpired());
        return isWorking;
    }
}
