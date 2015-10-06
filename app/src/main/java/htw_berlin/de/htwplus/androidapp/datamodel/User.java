package htw_berlin.de.htwplus.androidapp.datamodel;

public class User {

    private int mAccountId;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mStudycourse;

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

    public int getAccountId() {
        return mAccountId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getStudycourse() {
        return mStudycourse;
    }

    @Override
    public String toString() {
        return "" + mFirstName + " " + mLastName;
    }


}
