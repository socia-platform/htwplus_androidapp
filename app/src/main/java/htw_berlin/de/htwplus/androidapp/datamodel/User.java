package htw_berlin.de.htwplus.androidapp.datamodel;

/**
 * Created by tino on 28.06.15.
 */
public class User {

    private int accountId;
    private String firstName;
    private String lastName;
    private String email;
    private String studycourse;

    public User(int accId, String fName, String lName, String mail, String sCourse) {
        boolean inputOk = ((accId > 0 ) && (fName != null) && (lName != null) &&
                          (mail != null) && (sCourse != null));
        if (inputOk)
        {
            accountId = accId;
            firstName = fName;
            lastName = lName;
            email = mail;
            studycourse = sCourse;
        }
        else
            throw new IllegalArgumentException("Invalid arguments for user initialization.");
    }

    public int getAccountId() {
        return accountId;
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

    @Override
    public String toString() {
        return "" + firstName + " " + lastName;
    }


}
