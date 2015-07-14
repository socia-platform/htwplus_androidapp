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

/**
 * Created by tino on 14.07.15.
 */
public class PostAdapter extends ArrayAdapter<Post> {

    private Context context;
    private int layoutResourceId;
    private List<Post> posts;

    public PostAdapter(Context context, int layoutResourceId, List<Post> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.posts = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        Post post = posts.get(position);
        TextView userNameTextView = (TextView) row.findViewById(R.id.userNameTextView);
        TextView createDateTimeTextView = (TextView) row.findViewById(R.id.createDateTimeTextView);
        TextView postContentTextView = (TextView) row.findViewById(R.id.postContentTextView);
        TextView labelGroupNameTextView = (TextView) row.findViewById(R.id.labelGroupNameTextView);
        TextView groupNameTextView = (TextView) row.findViewById(R.id.groupNameTextView);
        if (post != null) {
            if (userNameTextView != null)
                if (post.isGroupPost())
                    userNameTextView.setText(String.valueOf(post.getOwnerId()));
                else
                    userNameTextView.setText(String.valueOf(post.getAccountId()));
            if (createDateTimeTextView != null)
                // Erstelldatum und -zeit werden (noch) nicht von der RestApi geliefert
                createDateTimeTextView.setText("01.01.1999 22:22 Uhr");
            if (postContentTextView != null)
                postContentTextView.setText(post.getContent());
            if ((labelGroupNameTextView != null) && (groupNameTextView != null) && (post.isGroupPost())) {
                labelGroupNameTextView.setVisibility(View.VISIBLE);
                groupNameTextView.setVisibility(View.VISIBLE);
                groupNameTextView.setText(String.valueOf(post.getGroupId()));
            }
        }
        return row;
    }
}
