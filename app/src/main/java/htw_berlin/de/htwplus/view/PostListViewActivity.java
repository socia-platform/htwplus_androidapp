package htw_berlin.de.htwplus.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hamnaberg.json.Collection;

import java.util.ArrayList;
import java.util.List;

import htw_berlin.de.htwplus.ApplicationController;
import htw_berlin.de.htwplus.R;
import htw_berlin.de.htwplus.datamodel.ApiError;
import htw_berlin.de.htwplus.datamodel.Post;
import htw_berlin.de.htwplus.util.JsonCollectionHelper;

public class PostListViewActivity extends Activity implements Response.Listener, Response.ErrorListener {

    private ArrayList<Post> mlist;
    private ArrayAdapter<Post> mAdapter;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list_view);
        mlist = new ArrayList<Post>();
        ApplicationController.getVolleyController().getPostsFromNewsstream(this, this, this);
        listview = (ListView) findViewById(R.id.list);
        mAdapter = new ArrayAdapter<Post>(this, android.R.layout.simple_list_item_1, mlist);
        listview.setAdapter(mAdapter);
        /*
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ShowUserActivity.class);
                intent.putExtra("Firstname", mlist.get(position).getFirstName());
                intent.putExtra("Lastname", mlist.get(position).getLastName());
                intent.putExtra("Email", mlist.get(position).getEmail());
                intent.putExtra("Class", mlist.get(position).getClass());
                UserListViewActivity.this.startActivity(intent);
            }
        });
        */
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
        try {
            Collection collection = JsonCollectionHelper.parse(response.toString());
            if (!JsonCollectionHelper.hasError(collection)) {
                List<Post> posts = JsonCollectionHelper.toPosts(collection);
                for (Post post : posts)
                    mlist.add(post);
            } else {
                ApiError apiError = JsonCollectionHelper.toError(collection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
