package htw_berlin.de.htwplus.androidapp.util;

import net.hamnaberg.funclite.Optional;
import net.hamnaberg.json.Collection;
import net.hamnaberg.json.Data;
import net.hamnaberg.json.Item;
import net.hamnaberg.json.Link;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Query;
import net.hamnaberg.json.Template;
import net.hamnaberg.json.Value;

import org.json.JSONException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import htw_berlin.de.htwplus.androidapp.ApplicationController;
import htw_berlin.de.htwplus.androidapp.datamodel.ApiError;
import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

/**
 * Created by tino on 28.06.15.
 */
public class JsonCollectionHelper {

    private JsonCollectionHelper() {
    }

    public static List<User> toUsers(Collection collection) {
        List<User> users = new ArrayList<User>();
        for (Item item : collection.getItems()) {
            Data data = item.getData();
            boolean hrefOk = (!item.getHref().isNone());
            boolean propertyOk = ((hasProperty("firstname", data))
                                 && (hasProperty("lastname", data))
                                 && (hasProperty("email", data))
                                 && (hasProperty("studycourse", data)));
            if (hrefOk && propertyOk) {
                URI resourceUri = item.getHref().get();
                String[] segments = resourceUri.getPath().split("/");
                String idStr = segments[segments.length-1];
                int accountId = Integer.parseInt(idStr);
                String firstName = data.propertyByName("firstname").get().hasValue() ? data.propertyByName("firstname").get().getValue().get().asString() : "";
                String lastName = data.propertyByName("lastname").get().hasValue() ? data.propertyByName("lastname").get().getValue().get().asString() : "";
                String email = data.propertyByName("email").get().hasValue() ? data.propertyByName("email").get().getValue().get().asString() : "";
                String course = data.propertyByName("studycourse").get().hasValue() ? data
                        .propertyByName("studycourse").get().getValue().get().asString() : "";
                users.add(new User(accountId, firstName, lastName, email, course));
            }
        }
        return users;
    }

    public static List<Post> toPosts(Collection collection) {
        List<Post> posts = new ArrayList<Post>();
        for (Item item : collection.getItems()) {
            Data data = item.getData();
            boolean hrefOk = (!item.getHref().isNone());
            boolean propertyOk = ((hasProperty("content", data)) && (hasProperty("parent_id", data))
                    && (hasProperty("group_id", data)) && (hasProperty("account_id", data))
                    && (hasProperty("owner_id", data)) && (hasProperty("created_at", data))
                    && (hasProperty("updated_at", data)));

            if (hrefOk && propertyOk) {
                URI resourceUri = item.getHref().get();
                String[] segments = resourceUri.getPath().split("/");
                String idStr = segments[segments.length-1];
                int postId = Integer.parseInt(idStr);
                String content = data.propertyByName("content").get().hasValue() ? data.propertyByName("content").get().getValue().get().asString() : "";
                int parentId = data.propertyByName("parent_id").get().hasValue() ? Integer.parseInt(data.propertyByName("parent_id").get().getValue().get().asString()) : -1;
                int groupId = data.propertyByName("group_id").get().hasValue() ? Integer.parseInt(data.propertyByName("group_id").get().getValue().get().asString()) : -1;
                int accountId = data.propertyByName("account_id").get().hasValue() ? Integer.parseInt(data.propertyByName("account_id").get().getValue().get().asString()) : -1;
                int ownerId = data.propertyByName("owner_id").get().hasValue() ? Integer.parseInt(data.propertyByName("owner_id").get().getValue().get().asString()) : -1;
                String creationDate = data.propertyByName("created_at").get().hasValue() ? data.propertyByName("created_at").get().getValue().get().asString() : "";
                posts.add(new Post(postId, content, accountId, ownerId, parentId, groupId, creationDate));
            }
        }
        return posts;
    }

    public static Collection buildPost(String content, Optional<Long> accountId,
                                       Optional<Long> ownerId, Optional<Long> parentId,
                                       Optional<Long> groupId) throws JSONException {
        String accessToken = ApplicationController.getSharedPrefController().getAccessToken();
        String apiUrl = ApplicationController.getSharedPrefController().getApiUrl().toString();
        URI resourceUri = URI.create(apiUrl + "users/" + String.valueOf(accountId.get()) +
                "?access_token=" + accessToken);
        List<Link> links = new ArrayList<Link>();
        List<Query> queries = new ArrayList<Query>();

        List<Item> items = new ArrayList<Item>();
        Property contentProp = Property.value("content", Optional.some("The content."), content);
        Property accIdProp = null;
        if (Optional.fromNullable(accountId).isNone())
            accIdProp = Property.value("account_id", Optional.some("The account id."), Optional.<Value>none());
        else
            accIdProp = Property.value("account_id", Optional.some("The account id."), accountId.get());
        Property ownerIdProp = null;
        if (Optional.fromNullable(ownerId).isNone())
            ownerIdProp = Property.value("owner_id", Optional.some("The owner id."), Optional.<Value>none());
        else
            ownerIdProp = Property.value("owner_id", Optional.some("The owner id."), ownerId.get());
        Property parentIdProp = null;
        if (Optional.fromNullable(parentId).isNone())
            parentIdProp = Property.value("parent_id", Optional.some("The parent id."), Optional.<Value>none());
        else
            parentIdProp = Property.value("parent_id", Optional.some("The parent id."), parentId.get());
        Property groupIdProp = null;
        if (Optional.fromNullable(groupId).isNone())
            groupIdProp = Property.value("group_id", Optional.some("The group id."), Optional.<Value>none());
        else
            groupIdProp = Property.value("group_id", Optional.some("The group id."), groupId.get());
        Item.Builder itemBuilder = Item.builder(resourceUri);
        itemBuilder.addProperty(contentProp);
        itemBuilder.addProperty(accIdProp);
        itemBuilder.addProperty(ownerIdProp);
        itemBuilder.addProperty(parentIdProp);
        itemBuilder.addProperty(groupIdProp);
        Item postItem = itemBuilder.build();
        items.add(postItem);

        Template template = Template.create();

        return Collection.create(resourceUri, links, items, queries, template, null);
    }

    public static ApiError toError(Collection collection) {
        net.hamnaberg.json.Error error = collection.getError().get();
        return new ApiError(error.getTitle(), error.getMessage(), error.getCode());
    }

    private static boolean hasProperty(String propertyName, Data data) {
        boolean hasProperty = false;
        if (!data.propertyByName(propertyName).isNone())
            hasProperty = true;
        return hasProperty;
    }

    public static boolean hasError(Collection collection) {
        return (collection.hasError() && (collection.getError().get().getCode() != null) &&
                (collection.getError().get().getMessage() != null) &&
                (collection.getError().get().getTitle() != null));
    }
}
