package htw_berlin.de.htwplus.datamodel;

/**
 * Created by tino on 28.06.15.
 */
public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String studycourse;

    public User(String fName, String lName, String mail, String sCourse) {
        boolean inputOk = ((fName != null) && (lName != null) &&
                          (mail != null) && (sCourse != null));
        if (inputOk)
        {
            firstName = fName;
            lastName = lName;
            email = mail;
            studycourse = sCourse;
        }
        else
            throw new IllegalArgumentException("Invalid arguments for user initialization.");
    }


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getStudycourse() {
        return studycourse;
    }
}
