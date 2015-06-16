package htw_berlin.de.htwplus;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.hamnaberg.json.Collection;
import net.hamnaberg.json.parser.CollectionParser;
import net.hamnaberg.funclite.Function;
import net.hamnaberg.funclite.FunctionalList;
import net.hamnaberg.funclite.Optional;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;


public class MainActivity extends Activity {

    boolean col = true;
    private InputStream dum;
    private String result = "";
    private DefaultHttpClient client;
    Collection collection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new DefaultHttpClient();
        try {
            collection = client.execute(new HttpGet(URI.create("http://localhost:9000/api/persons")), new CollectionResponseHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(collection.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class CollectionResponseHandler implements ResponseHandler<Collection> {
        @Override
        public Collection handleResponse(HttpResponse response) throws IOException {
            if (response.getStatusLine().getStatusCode() == 200) {
                if (isCollectionJSON(response)) {
                    InputStream content = null;
                    try {
                        content = response.getEntity().getContent();
                        return new CollectionParser().parse(content);
                    } finally {
                        if (content != null) {
                            content.close();
                        }
                    }
                }
            }
            throw new RuntimeException("No usable status here");
        }

        private boolean isCollectionJSON(HttpResponse response) {
            return response.getEntity() != null && "application/vnd.collection+json".equals(response.getEntity().getContentType().getValue());
        }
    }
}

