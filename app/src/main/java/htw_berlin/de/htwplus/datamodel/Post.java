package htw_berlin.de.htwplus.datamodel;

import net.hamnaberg.json.Item;
import net.hamnaberg.json.Link;
import net.hamnaberg.json.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tim on 01.07.15.
 */
public class Post implements Comparable<Post> {

    private int postId;
    private String content;
    private int accountId;
    private int ownerId;
    private int parentId;
    private int groupId;
    private List<Link> links;
    private List<Query> queries;
    private List<Item> items;

    public Post(int postId, String content, int accountId, int ownerId, int parentId, int groupId) {
        this.postId = postId;
        this.content = content;
        this.accountId = accountId;
        this.ownerId = ownerId;
        this.parentId = parentId;
        this.groupId = groupId;
        this.links = new ArrayList<Link>();
        this.items = new ArrayList<Item>();
        this.queries = new ArrayList<Query>();
    }

    public Post(int postId, String content, int accountId, int ownerId, int parentId, int groupId, List<Link> links, List<Query> queries, List<Item> items) {
        this.postId = postId;
        this.content = content;
        this.accountId = accountId;
        this.ownerId = ownerId;
        this.parentId = parentId;
        this.groupId = groupId;
        this.links = links;
        this.items = items;
        this.queries = queries;
    }

    // Setters and Getters

    public int getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Post{" +
                "content='" + content + '\'' +
                ", accountId=" + accountId +
                ", ownerId=" + ownerId +
                ", parentId=" + parentId +
                ", groupId=" + groupId +
                ", links=" + links +
                ", queries=" + queries +
                ", items=" + items +
                '}';
    }

    public boolean isCommentPost() {
        return (parentId > -1);
    }

    public boolean isGroupPost() {
        return ((groupId > -1) && (parentId == -1));
    }

    public boolean isGroupCommentPost() {
        return ((groupId > -1) && (parentId > -1));
    }

    @Override
    public int compareTo(Post post) {
        int result = 0;
        if (postId > post.getPostId())
            result = 1;
        else if (postId < post.getPostId())
            result = -1;
        return result;
    }
}
