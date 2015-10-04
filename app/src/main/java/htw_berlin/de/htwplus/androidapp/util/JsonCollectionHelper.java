package htw_berlin.de.htwplus.androidapp.util;

import net.hamnaberg.json.Collection;
import net.hamnaberg.json.Data;
import net.hamnaberg.json.Item;
import net.hamnaberg.json.parser.CollectionParser;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import htw_berlin.de.htwplus.androidapp.datamodel.ApiError;
import htw_berlin.de.htwplus.androidapp.datamodel.Post;
import htw_berlin.de.htwplus.androidapp.datamodel.User;

/**
 * Created by tino on 28.06.15.
 */
public class JsonCollectionHelper {

    private JsonCollectionHelper() {
    }

    public static Collection parse(String rawJsonCollection) throws IOException {
        CollectionParser cParser = new CollectionParser();
        return cParser.parse(rawJsonCollection);
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

    public static String extractAccessToken(Collection collection) {
        String accessToken = null;
        for (Item item : collection.getItems()) {
            Data data = item.getData();
            boolean propertyOk = (hasProperty("access_token", data));
            if (propertyOk) {
                accessToken = data.propertyByName("access_token").get().hasValue() ? data.propertyByName("access_token").get().getValue().get().asString() : null;
            }
        }
        return accessToken;
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
