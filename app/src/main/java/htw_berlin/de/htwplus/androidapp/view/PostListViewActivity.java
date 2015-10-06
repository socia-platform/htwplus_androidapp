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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import htw_berlin.de.htwplus.androidapp.Application;
import htw_berlin.de.htwplus.androidapp.PostAdapter;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

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
        initializeListViewComponents();
        initiateButtonClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Application.isWorkingState()) {
            Application.getVolleyController().getUsers(this, this, this);
            Application.getVolleyController().getPosts(this, this, this);
        } else
            Toast.makeText(getApplicationContext(), R.string.common_error_no_connection,
                    Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        String errorMessage = getText(R.string.error_unexpected_response).toString();
        if (error != null) {
            if ((error.getCause() != null) && (error.getCause().getMessage() != null)) {
                errorMessage += "\n" + error.getCause().getMessage();
            } else {
                if (error.getMessage() != null)
                    errorMessage += "\n" + error.getMessage();
            }
        }
        error.printStackTrace();
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
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

    private void initiateButtonClickListeners() {

        mCreateNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewPostButtonClick();
            }
        });
    }

    private void onCreateNewPostButtonClick() {
        String postMessage = mCreateNewPostEditText.getText().toString();
        if (!postMessage.isEmpty() && Application.isWorkingState()) {
            mCreateNewPostEditText.setText("");
            long currentUserId = Application.preferences().oAuth2().getCurrentUserId();
            Application.getVolleyController().addPost(postMessage, Optional.some(currentUserId),
                    Optional.some(currentUserId), null, null, REQUEST_TAG, this, this);
            Application.getVolleyController().getUsers(this, this, this);
            Application.getVolleyController().getPosts(this, this, this);
        } else if (!Application.isWorkingState()) {
            Toast.makeText(getApplicationContext(),
                    R.string.common_error_no_connection,
                    Toast.LENGTH_LONG).show();
        } else if (postMessage.isEmpty())
            Toast.makeText(getApplicationContext(),
                    R.string.common_error_no_message_input,
                    Toast.LENGTH_LONG).show();
    }

    private void initializeListViewComponents() {
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
