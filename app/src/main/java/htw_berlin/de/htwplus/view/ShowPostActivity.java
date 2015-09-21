package htw_berlin.de.htwplus.view;

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
import net.hamnaberg.json.Collection;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import htw_berlin.de.htwplus.ApplicationController;
import htw_berlin.de.htwplus.PostAdapter;
import htw_berlin.de.htwplus.R;
import htw_berlin.de.htwplus.datamodel.ApiError;
import htw_berlin.de.htwplus.datamodel.Post;
import htw_berlin.de.htwplus.datamodel.User;
import htw_berlin.de.htwplus.util.JsonCollectionHelper;

public class ShowPostActivity extends Activity implements Response.Listener, Response.ErrorListener {

    public static final String REQUEST_TAG = "ShowPostActivity";
    private int postId;
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
        mCommentListview = (ListView) findViewById(R.id.commentListView);
        mCreateNewCommentEditText = (EditText) findViewById(R.id.createNewCommentEditText);
        mCreateNewCommentButton = (Button) findViewById(R.id.createNewCommentButton);
        mPostCommentList = new ArrayList<Post>();
        mUserList = new ArrayList<User>();
        postId = getIntent().getExtras().getInt("postId");
        ApplicationController.getVolleyController().getUsers(this, this, this);
        ApplicationController.getVolleyController().getPostsFromNewsstream(this, this, this);
        mPostAdapter = new PostAdapter(this, R.layout.post_listview_item_row, mPostCommentList, mUserList);
        mCommentListview.setAdapter(mPostAdapter);
    }

    public void onCreateNewCommentButtonClick(View v) {
        try {
            String commentMessage = mCreateNewCommentEditText.getText().toString();
            if (!commentMessage.isEmpty()) {
                mCreateNewCommentEditText.setText("");
                ApplicationController.getVolleyController().addPost(commentMessage,
                                                                    Optional.some(28l),
                                                                    Optional.some(28l),
                                                                    Optional.some(new Long(postId)),
                                                                    null,
                                                                    REQUEST_TAG,
                                                                    this,
                                                                    this);
                ApplicationController.getVolleyController().getUsers(this, this, this);
                ApplicationController.getVolleyController().getPostsFromNewsstream(this, this, this);
            } else
                Toast.makeText(getApplicationContext(), "Bitte Antwort eingeben!", Toast.LENGTH_LONG).show();
        } catch (JSONException jex) {
            Toast.makeText(getApplicationContext(), "JSON parse Exception!\nSiehe konsole!", Toast.LENGTH_LONG).show();
            jex.printStackTrace();
        }
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
                if (collection.getHref().get().getPath().contains("api/posts"))
                    refreshPostData(JsonCollectionHelper.toPosts(collection));
                else if (collection.getHref().get().getPath().contains("api/users"))
                    refreshUserData(JsonCollectionHelper.toUsers(collection));
            } else {
                ApiError apiError = JsonCollectionHelper.toError(collection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshPostData(List<Post> posts) {
        mPostCommentList.clear();
        for (Post post : posts) {
            if ((post.isCommentPost()) && (post.getParentId() == postId))
                mPostCommentList.add(post);
            else if (post.getPostId() == postId)
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
