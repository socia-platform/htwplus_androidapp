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

import java.util.ArrayList;
import java.util.List;

import htw_berlin.de.htwplus.androidapp.Application;
import htw_berlin.de.htwplus.androidapp.PostAdapter;
import htw_berlin.de.htwplus.androidapp.R;
import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

/**
 * Represents the posting view of newsstream postings.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class PostListViewActivity extends Activity implements
        Response.Listener, Response.ErrorListener {

    /** Request tag, which indicates a http get-request to get all posting resources. */
    public static final String VOLLEY_ALL_POSTS_REQUEST_TAG = "VolleyAllPostsPostListView";

    /** Request tag, which indicates a http get-request to get all user resources. */
    public static final String VOLLEY_ALL_USERS_REQUEST_TAG = "VolleyAllUsersPostListView";

    /** Request tag, which indicates a http post-request add a new posting. */
    public static final String VOLLEY_NEW_POST_REQUEST_TAG = "VolleyNewPostPostListView";

    /** List of postings, which is hold by post adapter. */
    private ArrayList<Post> mPostlist;

    /** List of comment postings, which is hold by post adapter. */
    private ArrayList<Post> mPostCommentlist;

    /** List of users, which is hold by post adapter. */
    private ArrayList<User> mUserlist;

    /** Post adapter, which is hold by list view. */
    private PostAdapter mPostAdapter;

    /** List view of the view. */
    private ListView mListview;

    /** Create new posting edit field of the view. */
    private EditText mCreateNewPostEditText;

    /** Create new posting button of the view. */
    private Button mCreateNewPostButton;

    /**
     * Called if activity is creating.
     *
     * @param savedInstanceState
     */
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

    /**
     * Called if activity resuming.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (Application.isWorkingState()) {
            Application.network().getUsers(VOLLEY_ALL_POSTS_REQUEST_TAG, this, this);
            Application.network().getPosts(VOLLEY_ALL_POSTS_REQUEST_TAG, this, this);
        } else
            Toast.makeText(getApplicationContext(), R.string.common_error_no_connection,
                    Toast.LENGTH_LONG).show();
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
        mCreateNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewPostButtonClick();
            }
        });
    }

    /**
     * Called if create new posting button of the view was clicked.
     */
    private void onCreateNewPostButtonClick() {
        String postMessage = mCreateNewPostEditText.getText().toString();
        if (!postMessage.isEmpty() && Application.isWorkingState()) {
            mCreateNewPostEditText.setText("");
            long currentUserId = Application.preferences().oAuth2().getCurrentUserId();
            Application.network().addPost(postMessage, Optional.some(currentUserId),
                    Optional.some(currentUserId), null, null,
                    VOLLEY_NEW_POST_REQUEST_TAG, this, this);
            Application.network().getUsers(VOLLEY_ALL_USERS_REQUEST_TAG, this, this);
            Application.network().getPosts(VOLLEY_ALL_POSTS_REQUEST_TAG, this, this);
        } else if (!Application.isWorkingState()) {
            Toast.makeText(getApplicationContext(),
                    R.string.common_error_no_connection,
                    Toast.LENGTH_LONG).show();
        } else if (postMessage.isEmpty())
            Toast.makeText(getApplicationContext(),
                    R.string.common_error_no_message_input,
                    Toast.LENGTH_LONG).show();
    }

    /**
     * Initializes all list view components.
     */
    private void initializeListViewComponents() {
        mListview = (ListView) findViewById(R.id.list);
        mPostAdapter = new PostAdapter(this, R.layout.post_listview_item_row, mPostlist, mUserlist);
        mListview.setAdapter(mPostAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ShowPostActivity.class);
                intent.putExtra("postId", mPostlist.get(position).getPostId());
                PostListViewActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Refreshes the posting data of the list view component with the given list of posting.
     *
     * @param posts List of posting to be refreshed
     */
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

    /**
     * Refreshes the user data of the list view component with the given list of user.
     *
     * @param users List of user to be refreshed
     */
    private void refreshUserData(List<User> users) {
        mUserlist.clear();
        for (User user : users)
            mUserlist.add(user);
        mPostAdapter.notifyDataSetChanged();
    }

}
