package htw_berlin.de.htwplus.util;

import com.fasterxml.jackson.databind.util.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.hamnaberg.json.Collection;
import net.hamnaberg.json.Data;
import net.hamnaberg.json.Item;
import net.hamnaberg.json.parser.CollectionParser;

import htw_berlin.de.htwplus.datamodel.ApiError;
import htw_berlin.de.htwplus.datamodel.Post;
import htw_berlin.de.htwplus.datamodel.User;

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
            boolean propertyOk = ((hasProperty("firstname", data)) && (hasProperty("lastname", data))
                                 && (hasProperty("email", data)) && (hasProperty("studycourse", data)));
            if (propertyOk) {
                String firstName = data.propertyByName("firstname").get().hasValue() ? firstName = data.propertyByName("firstname").get().getValue().get().asString() : "";
                String lastName = data.propertyByName("lastname").get().hasValue() ? data.propertyByName("lastname").get().getValue().get().asString() : "";
                String email = data.propertyByName("email").get().hasValue() ? email = data.propertyByName("email").get().getValue().get().asString() : "";
                String course = data.propertyByName("studycourse").get().hasValue() ? data.propertyByName("studycourse").get().getValue().get().asString() : "";
                users.add(new User(firstName, lastName, email, course));
            }
        }
        return users;
    }

    public static List<Post> toPosts(Collection collection) {
        List<Post> posts = new ArrayList<Post>();
        for (Item item : collection.getItems()) {
            Data data = item.getData();
            /*
            boolean propertyOk = ((hasProperty("firstname", data)) && (hasProperty("lastname", data))
                    && (hasProperty("email", data)) && (hasProperty("studycourse", data)));
            if (propertyOk) {
                String firstName = data.propertyByName("firstname").get().hasValue() ? firstName = data.propertyByName("firstname").get().getValue().get().asString() : "";
                String lastName = data.propertyByName("lastname").get().hasValue() ? data.propertyByName("lastname").get().getValue().get().asString() : "";
                String email = data.propertyByName("email").get().hasValue() ? email = data.propertyByName("email").get().getValue().get().asString() : "";
                String course = data.propertyByName("studycourse").get().hasValue() ? data.propertyByName("studycourse").get().getValue().get().asString() : "";
                posts.add(new User(firstName, lastName, email, course));
            }
            */
        }
        return posts;
    }

    public static ApiError toError(Collection collection) {
        net.hamnaberg.json.Error error = collection.getError().get();
        return new ApiError(error.getTitle(), error.getMessage(), error.getCode());
    }

    private static boolean hasProperty(String propertyName, Data data) {
        boolean hasProperty = false;
        if (!data.propertyByName(propertyName).isNone());
            hasProperty = true;
        return hasProperty;
    }

    public static boolean hasError(Collection collection) {
        return (collection.hasError() && (collection.getError().get().getCode() != null) &&
                (collection.getError().get().getMessage() != null) &&
                (collection.getError().get().getTitle() != null));
    }
}
