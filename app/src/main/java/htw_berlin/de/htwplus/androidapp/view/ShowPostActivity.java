package htw_berlin.de.htwplus.androidapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.hamnaberg.funclite.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import htw_berlin.de.htwplus.androidapp.Application;
import htw_berlin.de.htwplus.androidapp.PostAdapter;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

public class ShowPostActivity extends Activity implements
        Response.Listener, Response.ErrorListener {

    public static final String VOLLEY_ALL_POSTS_REQUEST_TAG = "VolleyAllPostsShowPost";
    public static final String VOLLEY_ALL_USERS_REQUEST_TAG = "VolleyAllUsersShowPost";
    public static final String VOLLEY_NEW_POST_REQUEST_TAG = "VolleyNewPostShowPost";
    private int mPostId;
    private List<Post> mPostCommentList;
    private List<User> mUserList;
    private PostAdapter mPostAdapter;
    private ListView mCommentListview;
    private EditText mCreateNewCommentEditText;
    private Button mCreateNewCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);
        mCreateNewCommentEditText = (EditText) findViewById(R.id.createNewCommentEditText);
        mCreateNewCommentButton = (Button) findViewById(R.id.createNewCommentButton);
        initializeListViewComponents();
        initiateButtonClickListeners();
        mPostId = getIntent().getExtras().getInt("postId");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Application.isWorkingState()) {
            Application.network().getUsers(VOLLEY_ALL_USERS_REQUEST_TAG, this, this);
            Application.network().getPosts(VOLLEY_ALL_POSTS_REQUEST_TAG, this, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Application.network().cancelRequest(VOLLEY_ALL_POSTS_REQUEST_TAG);
        Application.network().cancelRequest(VOLLEY_ALL_USERS_REQUEST_TAG);
        Application.network().cancelRequest(VOLLEY_NEW_POST_REQUEST_TAG);
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
                else
                    errorMessage += "\n" + error.toString();
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
        mCreateNewCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewCommentButtonClick();
            }
        });
    }

    private void onCreateNewCommentButtonClick() {
        String commentMessage = mCreateNewCommentEditText.getText().toString();
        if (!commentMessage.isEmpty() && Application.isWorkingState()) {
            mCreateNewCommentEditText.setText("");
            long currentUserId = Application.preferences().oAuth2().getCurrentUserId();
            Application.network().addPost(commentMessage, Optional.some(currentUserId),
                    Optional.some(currentUserId), Optional.some(new Long(mPostId)), null,
                    VOLLEY_NEW_POST_REQUEST_TAG, this, this);
            Application.network().getUsers(VOLLEY_ALL_USERS_REQUEST_TAG, this, this);
            Application.network().getPosts(VOLLEY_ALL_POSTS_REQUEST_TAG, this, this);
        } else if (!Application.isWorkingState()) {
            Toast.makeText(getApplicationContext(),
                    R.string.common_error_no_connection,
                    Toast.LENGTH_LONG).show();
        } else if (commentMessage.isEmpty())
            Toast.makeText(getApplicationContext(),
                    R.string.common_error_no_message_input,
                    Toast.LENGTH_LONG).show();
    }

    private void initializeListViewComponents() {
        mCommentListview = (ListView) findViewById(R.id.commentListView);
        mPostCommentList = new ArrayList<Post>();
        mUserList = new ArrayList<User>();
        mPostAdapter = new PostAdapter(this, R.layout.post_listview_item_row,
                mPostCommentList, mUserList);
        mCommentListview.setAdapter(mPostAdapter);
    }

    private void refreshPostData(List<Post> posts) {
        mPostCommentList.clear();
        for (Post post : posts) {
            if ((post.isCommentPost()) && (post.getParentId() == mPostId))
                mPostCommentList.add(post);
            else if (post.getPostId() == mPostId)
                mPostCommentList.add(post);
        }
        Collections.sort(mPostCommentList);
        mPostAdapter.notifyDataSetChanged();
    }

    private void refreshUserData(List<User> users) {
        mUserList.clear();
        for (User user : users)
            mUserList.add(user);
        mPostAdapter.notifyDataSetChanged();
    }

}
