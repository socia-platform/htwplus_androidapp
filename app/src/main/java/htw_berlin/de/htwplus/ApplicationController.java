package htw_berlin.de.htwplus;

import android.app.Application;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

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
    private static URL apiUrl;
    private static VolleyNetworkController vncInstance;

    // "Konstruktor"
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        vncInstance = VolleyNetworkController.getInstance(getApplicationContext());

        try {
            apiUrl = new URL("http://192.168.0.134:9000/api/");
        } catch (MalformedURLException muex) {
            Toast.makeText(getApplicationContext(), muex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static synchronized ApplicationController getInstance() {
        return mInstance;
    }

    public static URL getApiUrl() {
        return apiUrl;
    }

    public static VolleyNetworkController getVolleyController() {
        return vncInstance;
    }

}
