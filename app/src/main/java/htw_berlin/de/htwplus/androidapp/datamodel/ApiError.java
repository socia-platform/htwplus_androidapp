package htw_berlin.de.htwplus.androidapp.datamodel;

public class ApiError {

    private String mTitle;
    private String mMessage;
    private String mCode;

    public ApiError(String aTitle, String aMessage, String aCode) {
        boolean inputOk = ((aTitle != null) && (aMessage != null) &&
                          (aCode != null));
        if(inputOk)
        {
            mTitle = aTitle;
            mMessage = aMessage;
            mCode = aCode;
        }
        else
            throw new IllegalArgumentException("Invalid arguments for apierror initialization.");
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public String toString() {
        return "[" + mCode + "] " + mTitle + ": " + mMessage;
    }
}
