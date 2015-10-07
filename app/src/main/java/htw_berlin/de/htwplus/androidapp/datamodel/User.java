package htw_berlin.de.htwplus.androidapp.datamodel;

/**
 * Represents an user resource of HTWPLUS RESTful API.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class User {

    /** ID of the user account. */
    private int mAccountId;

    /** First name of the user. */
    private String mFirstName;

    /** Last name of the user. */
    private String mLastName;

    /** Email of the User. */
    private String mEmail;

    /** Study course of the User. */
    private String mStudycourse;

    /**
     * Creates a new user with the given account id, first name, last name, email and study course.
     *
     * @param fName First name of user
     * @param lName Last name of user
     * @param mail Email of user
     * @param sCourse Study course of User
     *
     * @throws IllegalArgumentException Throws if account id less or equal than 0 or first name,
     * last name, email or study course are null.
     */
    public User(int accId, String fName, String lName, String mail, String sCourse) {
        boolean inputOk = ((accId > 0 ) && (fName != null) && (lName != null) &&
                          (mail != null) && (sCourse != null));
        if (inputOk)
        {
            mAccountId = accId;
            mFirstName = fName;
            mLastName = lName;
            mEmail = mail;
            mStudycourse = sCourse;
        }
        else
            throw new IllegalArgumentException("Invalid arguments for user initialization.");
    }

    /**
     * Returns the user account id.
     *
     * @return Account id of the user.
     */
    public int getAccountId() {
        return mAccountId;
    }

    /**
     * Returns the user first name.
     *
     * @return First name of the user.
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Returns the user last name.
     *
     * @return Last name of the user.
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Returns the user email.
     *
     * @return Email of the user.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Returns the user study course.
     *
     * @return Study course of the user.
     */
    public String getStudycourse() {
        return mStudycourse;
    }

    /**
     * Returns the user full name.
     *
     * @return Full name of the user.
     */
    @Override
    public String toString() {
        return "" + mFirstName + " " + mLastName;
    }
}
