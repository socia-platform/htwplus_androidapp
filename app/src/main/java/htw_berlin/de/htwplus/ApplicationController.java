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
 * Created by tino on 23.06.15.
 */
public class ApplicationController extends Application {

    private static ApplicationController mInstance;
    private static URL apiUrl;

    // "Konstruktor"
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        VolleyNetworkController.getInstance(getApplicationContext());

        try {
            apiUrl = new URL("http://192.168.0.212:9000/api/");
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

}
