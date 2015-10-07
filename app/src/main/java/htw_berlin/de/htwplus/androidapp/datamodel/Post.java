package htw_berlin.de.htwplus.androidapp.datamodel;

/**
 * Represents a posting of HTWPLUS RESTful API.
 * Postings are be compared by their id.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class Post implements Comparable<Post> {

    /** ID of the posting. */
    private int mPostId;

    /** Content of the posting. */
    private String mContent;

    /** User's account id of the posting. */
    private int mAccountId;

    /** Owner id of the posting. */
    private int mOwnerId;

    /** Parent id of the posting. */
    private int mParentId;

    /** Group id of the posting. */
    private int mGroupId;

    /** Creation date of the posting. */
    private String mCreationDate;

    /**
     * Creates a new posting with the given post id, content, account id, owner id, parent id,
     * group id and creation date.
     *
     * @param postId Id of posting
     * @param content Content of posting
     * @param accountId User's account id of posting
     * @param ownerId Owner id of posting
     * @param parentId Parent id of posting
     * @param groupId Group id of posting
     * @param creationDate Creation date of posting
     */
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

    /**
     * Returns the posting id.
     *
     * @return Id of the posting.
     */
    public int getPostId() {
        return mPostId;
    }

    /**
     * Returns the posting content.
     *
     * @return Content of the posting.
     */
    public String getContent() {
        return mContent;
    }

    /**
     * Sets the posting content.
     *
     * @param content Content to be set of the posting.
     */
    public void setContent(String content) {
        this.mContent = content;
    }

    /**
     * Returns the posting account id of the user.
     *
     * @return User's account id of the posting.
     */
    public int getAccountId() {
        return mAccountId;
    }

    /**
     * Sets the posting account id of the user.
     *
     * @param accountId User's account id to be set of the posting.
     */
    public void setAccountId(int accountId) {
        this.mAccountId = accountId;
    }

    /**
     * Returns the posting owner id.
     *
     * @return Owner id of the posting.
     */
    public int getOwnerId() {
        return mOwnerId;
    }

    /**
     * Sets the posting owner id.
     *
     * @param ownerId Owner id to be set of the posting.
     */
    public void setOwnerId(int ownerId) {
        this.mOwnerId = ownerId;
    }

    /**
     * Returns the posting parent id.
     *
     * @return Parent id of the posting.
     */
    public int getParentId() {
        return mParentId;
    }

    /**
     * Sets the posting parent id.
     *
     * @param parentId Parent id to be set of the posting.
     */
    public void setParentId(int parentId) {
        this.mParentId = parentId;
    }

    /**
     * Returns the posting group id.
     *
     * @return Group id of the posting.
     */
    public int getGroupId() {
        return mGroupId;
    }

    /**
     * Sets the posting group id.
     *
     * @param groupId Group id to be set of the posting.
     */
    public void setGroupId(int groupId) {
        this.mGroupId = groupId;
    }

    /**
     * Checks if this posting a comment (answer of a posting).
     *
     * @return True if is a comment post otherwise false.
     */
    public boolean isCommentPost() {
        return (mParentId > -1);
    }

    /**
     * Checks if this posting a group posting.
     *
     * @return True if is a group posting otherwise false.
     */
    public boolean isGroupPost() {
        return ((mGroupId > -1) && (mParentId == -1));
    }

    /**
     * Checks if this posting is a group comment (answer of a group posting).
     *
     * @return True if is a group comment otherwise false.
     */
    public boolean isGroupCommentPost() {
        return ((mGroupId > -1) && (mParentId > -1));
    }

    /**
     * Returns the posting creation date.
     *
     * @return Creation date of the posting in the format yyyy-mm-dd H:mm:ss.
     */
    public String getCreationDate() {
        return mCreationDate;
    }

    /**
     * Compares this posting with the given posting. <br />
     * If the id of this posting greater than the given one then 1. <br />
     * If the id of this posting equal the given one then 0. <br />
     * If the id of this posting less than the given one then -1.
     *
     * @param post Posting to be compare with this posting.
     *
     * @return
     */
    @Override
    public int compareTo(Post post) {
        int result = 0;
        if (mPostId > post.getPostId())
            result = 1;
        else if (mPostId < post.getPostId())
            result = -1;
        return result;
    }
}
