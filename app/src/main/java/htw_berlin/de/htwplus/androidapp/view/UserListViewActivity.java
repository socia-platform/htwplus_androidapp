package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hamnaberg.json.Collection;

import java.util.ArrayList;
import java.util.List;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.ApiError;
import htw_berlin.de.htwplus.androidapp.datamodel.User;
import htw_berlin.de.htwplus.androidapp.util.JsonCollectionHelper;

public class UserListViewActivity extends Activity implements Response.Listener, Response.ErrorListener {

    private ArrayList<User> mlist;
    private ArrayAdapter<User> mAdapter;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);
        mlist = new ArrayList<User>();
        ApplicationController.getVolleyController().getUsers(this, this, this);
        listview = (ListView) findViewById(R.id.list);
        mAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, mlist);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ShowUserActivity.class);
                intent.putExtra("accountId", mlist.get(position).getAccountId());
                UserListViewActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error != null) {
            Toast.makeText(getApplicationContext(), "Error\n", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error is emtpy\n", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponse(Object response) {
        mlist.addAll((List<User>)response);
        mAdapter.notifyDataSetChanged();
    }
}
