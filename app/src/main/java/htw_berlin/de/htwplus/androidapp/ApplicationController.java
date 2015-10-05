package htw_berlin.de.htwplus.androidapp;

import android.app.Application;

import java.util.Date;

/**
 * Von dieser Klasse wird bei Start der App nur ein Objekt instanziiert,
 * auf welches dann global zugegriffen werden kann (Singleton). Diese
 * Klasse ist in der AndroidManifext.xml unter <application> eingetragen.
 *
 *
 *
 * 0.0.2.1	    Router/gateway address
 * 10.0.2.2	    Special alias to your host loopback interface (i.e., 127.0.0.1 on your development machine)
 * 10.0.2.3	    First DNS server
 * 10.0.2.15	The emulated device's own network/ethernet interface
 * 127.0.0.1	The emulated device's own loopback interface
 *
 * Created by tino on 23.06.15.
 */
public class ApplicationController extends Application {

    private static ApplicationController mInstance;
    private static VolleyNetworkController vncInstance;
    private static SharedPreferencesController spcInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        vncInstance = VolleyNetworkController.getInstance(getApplicationContext());
        spcInstance = SharedPreferencesController.getInstance(getApplicationContext(),
                                                                "AppPreferences",
                                                                MODE_PRIVATE);
    }

    public static synchronized ApplicationController getInstance() {
        return mInstance;
    }

    public static VolleyNetworkController getVolleyController() {
        return vncInstance;
    }

    public static SharedPreferencesController getSharedPrefController() {
        return spcInstance;
    }

    public boolean isWorkingState() {
        boolean isWorking = (getSharedPrefController().apiRoute().hasApiUrl() &&
                             !getSharedPrefController().oAuth2().isAccessTokenExpired());
        return isWorking;
    }
}
