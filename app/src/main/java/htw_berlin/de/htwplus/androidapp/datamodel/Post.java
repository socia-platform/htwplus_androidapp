package htw_berlin.de.htwplus.androidapp.datamodel;

public class Post implements Comparable<Post> {

    private int mPostId;
    private String mContent;
    private int mAccountId;
    private int mOwnerId;
    private int mParentId;
    private int mGroupId;
    private String mCreationDate;

    public Post(int postId, String content, int accountId, int ownerId,
                int parentId, int groupId, String creationDate) {
        this.mPostId = postId;
        this.mContent = content;
        this.mAccountId = accountId;
        this.mOwnerId = ownerId;
        this.mParentId = parentId;
        this.mGroupId = groupId;
        this.mCreationDate = creationDate;
    }

    public int getPostId() {
        return mPostId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public int getAccountId() {
        return mAccountId;
    }

    public void setAccountId(int accountId) {
        this.mAccountId = accountId;
    }

    public int getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(int ownerId) {
        this.mOwnerId = ownerId;
    }

    public int getParentId() {
        return mParentId;
    }

    public void setParentId(int parentId) {
        this.mParentId = parentId;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public void setGroupId(int groupId) {
        this.mGroupId = groupId;
    }

    public boolean isCommentPost() {
        return (mParentId > -1);
    }

    public boolean isGroupPost() {
        return ((mGroupId > -1) && (mParentId == -1));
    }

    public boolean isGroupCommentPost() {
        return ((mGroupId > -1) && (mParentId > -1));
    }

    @Override
    public int compareTo(Post post) {
        int result = 0;
        if (mPostId > post.getPostId())
            result = 1;
        else if (mPostId < post.getPostId())
            result = -1;
        return result;
    }

    public String getCreationDate() {
        return mCreationDate;
    }
}
