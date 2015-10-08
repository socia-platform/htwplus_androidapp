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

/**
 * Represents the detail view of a posting.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class ShowPostActivity extends Activity implements
        Response.Listener, Response.ErrorListener {

    /** Request tag, which indicates a http get-request to get all posting resources. */
    public static final String VOLLEY_ALL_POSTS_REQUEST_TAG = "VolleyAllPostsShowPost";

    /** Request tag, which indicates a http get-request to get all user resources. */
    public static final String VOLLEY_ALL_USERS_REQUEST_TAG = "VolleyAllUsersShowPost";

    /** Request tag, which indicates a http post-request add a new posting. */
    public static final String VOLLEY_NEW_POST_REQUEST_TAG = "VolleyNewPostShowPost";

    /** Id of the parent posting. */
    private int mPostId;

    /** List of comment postings, which is hold by post adapter. */
    private List<Post> mPostCommentList;

    /** List of users, which is hold by post adapter. */
    private List<User> mUserList;

    /** Post adapter, which is hold by list view. */
    private PostAdapter mPostAdapter;

    /** List view of the view. */
    private ListView mCommentListview;

    /** Create new comment posting edit field of the view. */
    private EditText mCreateNewCommentEditText;

    /** Create new comment posting button of the view. */
    private Button mCreateNewCommentButton;

    /**
     * Called if activity is creating.
     *
     * @param savedInstanceState
     */
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

    /**
     * Called if activity resuming.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (Application.isWorkingState()) {
            Application.network().getUsers(VOLLEY_ALL_USERS_REQUEST_TAG, this, this);
            Application.network().getPosts(VOLLEY_ALL_POSTS_REQUEST_TAG, this, this);
        }
    }

    /**
     * Called if activity is stopping.
     */
    @Override
    public void onStop() {
        super.onStop();
        Application.network().cancelRequest(VOLLEY_ALL_POSTS_REQUEST_TAG);
        Application.network().cancelRequest(VOLLEY_ALL_USERS_REQUEST_TAG);
        Application.network().cancelRequest(VOLLEY_NEW_POST_REQUEST_TAG);
    }

    /**
     * Called if a http response is received, which is not ok.
     *
     * @param error
     */
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

    /**
     * Called if a http response is received, which is ok.
     *
     * @param response
     */
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

    /**
     * Initiates listeners for all buttons of the view.
     */
    private void initiateButtonClickListeners() {
        mCreateNewCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewCommentButtonClick();
            }
        });
    }

    /**
     * Called if create new comment posting button of the view was clicked.
     */
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

    /**
     * Initializes all list view components.
     */
    private void initializeListViewComponents() {
        mCommentListview = (ListView) findViewById(R.id.commentListView);
        mPostCommentList = new ArrayList<Post>();
        mUserList = new ArrayList<User>();
        mPostAdapter = new PostAdapter(this, R.layout.post_listview_item_row,
                mPostCommentList, mUserList);
        mCommentListview.setAdapter(mPostAdapter);
    }

    /**
     * Refreshes the posting data of the list view component with the given list of posting.
     *
     * @param posts List of posting to be refreshed
     */
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

    /**
     * Refreshes the user data of the list view component with the given list of user.
     *
     * @param users List of user to be refreshed
     */
    private void refreshUserData(List<User> users) {
        mUserList.clear();
        for (User user : users)
            mUserList.add(user);
        mPostAdapter.notifyDataSetChanged();
    }

}
