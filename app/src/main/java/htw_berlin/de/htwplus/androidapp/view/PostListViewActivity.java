package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hamnaberg.funclite.Optional;
import net.hamnaberg.json.Collection;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.PostAdapter;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.ApiError;
import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;
import htw_berlin.de.htwplus.androidapp.util.JsonCollectionHelper;

public class PostListViewActivity extends Activity implements Response.Listener, Response.ErrorListener {

    public static final String REQUEST_TAG = "PostListViewActivity";
    private ArrayList<Post> mPostlist;
    private ArrayList<Post> mPostCommentlist;
    private ArrayList<User> mUserlist;
    private PostAdapter mPostAdapter;
    private ListView mlistview;
    private EditText mCreateNewPostEditText;
    private Button mCreateNewPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list_view);
        mCreateNewPostEditText = (EditText) findViewById(R.id.createNewPostEditText);
        mCreateNewPostButton = (Button) findViewById(R.id.createNewPostButton);
        mPostlist = new ArrayList<Post>();
        mPostCommentlist = new ArrayList<Post>();
        mUserlist = new ArrayList<User>();
        ApplicationController.getVolleyController().getUsers(this, this, this);
        ApplicationController.getVolleyController().getPostsFromNewsstream(this, this, this);
        mlistview = (ListView) findViewById(R.id.list);
        mPostAdapter = new PostAdapter(this, R.layout.post_listview_item_row, mPostlist, mUserlist);
        mlistview.setAdapter(mPostAdapter);
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ShowPostActivity.class);
                intent.putExtra("postId", mPostlist.get(position).getPostId());
                PostListViewActivity.this.startActivity(intent);
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
        if (response != null) {
            List<Object> objects = (List<Object>) response;
            if (objects.size() > 0) {
                if (objects.get(0).getClass().equals(Post.class))
                    refreshPostData((List<Post>) (Object) objects);
                else if (objects.get(0).getClass().equals(User.class))
                    refreshUserData((List<User>) (Object) objects);
            }
        }
    }

    public void onCreateNewPostButtonClick(View v) {
        try {
            String postMessage = mCreateNewPostEditText.getText().toString();
            if (!postMessage.isEmpty()) {
                mCreateNewPostEditText.setText("");
                ApplicationController.getVolleyController().addPost(postMessage, Optional.some(57l),
                        Optional.some(57l), null, null, REQUEST_TAG, this, this);
                ApplicationController.getVolleyController().getUsers(this, this, this);
                ApplicationController.getVolleyController().getPostsFromNewsstream(this, this, this);
            } else
                Toast.makeText(getApplicationContext(), "Bitte Post-Message eingeben!", Toast.LENGTH_LONG).show();
        } catch (JSONException jex) {
            Toast.makeText(getApplicationContext(), "JSON parse Exception!\nSiehe konsole!", Toast.LENGTH_LONG).show();
            jex.printStackTrace();
        }
    }

    private void refreshPostData(List<Post> posts) {
        mPostCommentlist.clear();
        mPostlist.clear();
        for (Post post : posts) {
            if (post.isCommentPost())
                mPostCommentlist.add(post);
            else
                mPostlist.add(post);
        }
        mPostAdapter.notifyDataSetChanged();
    }

    private void refreshUserData(List<User> users) {
        mUserlist.clear();
        for (User user : users)
            mUserlist.add(user);
        mPostAdapter.notifyDataSetChanged();
    }

}
