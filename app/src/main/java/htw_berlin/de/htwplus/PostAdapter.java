package htw_berlin.de.htwplus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import htw_berlin.de.htwplus.datamodel.Post;
import htw_berlin.de.htwplus.datamodel.User;

/**
 * Created by tino on 14.07.15.
 */
public class PostAdapter extends ArrayAdapter<Post> {

    private Context context;
    private int layoutResourceId;
    private List<Post> posts;
    private List<User> users;

    public PostAdapter(Context context, int layoutResourceId, List<Post> data, List<User> userData) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.posts = data;
        this.users = userData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        TextView userNameTextView = (TextView) row.findViewById(R.id.userNameTextView);
        TextView createDateTimeTextView = (TextView) row.findViewById(R.id.createDateTimeTextView);
        TextView postContentTextView = (TextView) row.findViewById(R.id.postContentTextView);
        TextView labelGroupNameTextView = (TextView) row.findViewById(R.id.labelGroupNameTextView);
        TextView groupNameTextView = (TextView) row.findViewById(R.id.groupNameTextView);
        Post post = posts.get(position);
        if (post != null) {
            if (userNameTextView != null) {
                User user = null;
                if (post.isCommentPost() || post.isGroupCommentPost() || post.isGroupPost())
                    user = findUser(post.getOwnerId());
                else
                    user = findUser(post.getAccountId());
                String userName = (user != null) ? user.toString() : "unkown";
                userNameTextView.setText(userName);
            }
            if (createDateTimeTextView != null)
                createDateTimeTextView.setText(post.getCreationDate());
            if (postContentTextView != null)
                postContentTextView.setText(post.getContent());
            if ((labelGroupNameTextView != null) && (groupNameTextView != null) &&
                    ((post.isGroupPost()) || (post.isGroupCommentPost()))) {
                labelGroupNameTextView.setVisibility(View.VISIBLE);
                groupNameTextView.setVisibility(View.VISIBLE);
                groupNameTextView.setText(String.valueOf(post.getGroupId()));
            } else {
                labelGroupNameTextView.setVisibility(View.INVISIBLE);
                groupNameTextView.setVisibility(View.INVISIBLE);
            }
        }
        return row;
    }

    private User findUser(long accountId) {
        User user = null;
        for (User aUser : users)
            if (aUser.getAccountId() == accountId) {
                user = aUser;
                break;
            }
        return user;
    }
}
