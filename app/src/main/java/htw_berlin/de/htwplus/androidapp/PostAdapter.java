package htw_berlin.de.htwplus.androidapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

/**
 * A overwritten BaseAdapter that is backed by a list of posting objects and a list of user
 * objects. This adapter is intended for the use of a listview which displays postings with user
 * information.<br /><br />
 *
 * Is currently needed for <i>postlistviewactivity</i> and <i>showpostactivity</i>.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class PostAdapter extends ArrayAdapter<Post> {

    /** Context of the adapter. */
    private Context mContext;

    /** Resource id of layout, which represents the view to be filled and displayed. */
    private int mLayoutResourceId;

    /** List of postings to be displayed. */
    private List<Post> mPosts;

    /** List of users to be displayed with postings. */
    private List<User> mUsers;

    /**
     * Creates a new post adapter with the given context, layout resource id, list of postings
     * and list of users.
     *
     * @param context Context to be used
     * @param layoutResourceId Layout resource id, which represents the view to be filled and
     *                         displayed
     * @param data List of postings to be displayed
     * @param userData List of users to be displayed with postings
     */
    public PostAdapter(Context context, int layoutResourceId,
                       List<Post> data, List<User> userData) {
        super(context, layoutResourceId, data);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
        this.mPosts = data;
        this.mUsers = userData;
    }

    /**
     * Gets a View that displays the data at the specified position in the data set.<br />
     * This view corresponds the given layout resource id at initialization.
     *
     * @param position The position of the item within the adapter's data set of the item whose
     *                 view we want.
     * @param convertView The old view to reuse, if possible
     * @param parent The parent that this view will eventually be attached to
     *
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }
        TextView userNameTextView = (TextView) row.findViewById(R.id.userNameTextView);
        TextView createDateTimeTextView = (TextView) row.findViewById(R.id.createDateTimeTextView);
        TextView postContentTextView = (TextView) row.findViewById(R.id.postContentTextView);
        TextView labelGroupNameTextView = (TextView) row.findViewById(R.id.labelGroupNameTextView);
        TextView groupNameTextView = (TextView) row.findViewById(R.id.groupNameTextView);
        Post post = mPosts.get(position);
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

    /**
     * Searches for the user object, which matches the given account id.<br />
     * It is searched in the list of users to be given at initialization.
     *
     * @param accountId User's account id to be searched for
     *
     * @return Matched user object or null if nothing was matched.
     */
    private User findUser(long accountId) {
        User user = null;
        for (User aUser : mUsers)
            if (aUser.getAccountId() == accountId) {
                user = aUser;
                break;
            }
        return user;
    }
}
