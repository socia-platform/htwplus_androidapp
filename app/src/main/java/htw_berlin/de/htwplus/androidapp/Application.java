package htw_berlin.de.htwplus.androidapp;

public class Application extends android.app.Application {

    private static Application mInstance;
    private static VolleyNetworkController mVncInstance;
    private static SharedPreferencesProxy mSpcInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mVncInstance = VolleyNetworkController.getInstance(getApplicationContext());
        mSpcInstance = SharedPreferencesProxy.getInstance(getApplicationContext());
    }

    public static synchronized Application getInstance() {
        return mInstance;
    }

    public static VolleyNetworkController network() {
        return mVncInstance;
    }

    public static SharedPreferencesProxy preferences() {
        return mSpcInstance;
    }

    public static boolean isWorkingState() {
        boolean isWorking = (preferences().apiRoute().hasApiUrl() &&
                             !preferences().oAuth2().isAccessTokenExpired());
        return isWorking;
    }
}
