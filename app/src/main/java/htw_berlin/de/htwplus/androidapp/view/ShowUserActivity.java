package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import htw_berlin.de.htwplus.androidapp.R;

public class ShowUserActivity extends Activity {

    private TextView ViewName;
    private TextView ViewEmail;
    private TextView ViewClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);
        ViewName = (TextView) findViewById(R.id.viewName);
        ViewEmail = (TextView) findViewById(R.id.viewEmail);
        ViewClass = (TextView) findViewById(R.id.viewClass);
        ViewName.setText(getIntent().getExtras().getString("Firstname") + " " + getIntent().getExtras().getString("Lastname") );
        ViewEmail.setText(getIntent().getExtras().getString("Email"));
        ViewClass.setText(getIntent().getExtras().getString("Class"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_user, menu);
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
}
