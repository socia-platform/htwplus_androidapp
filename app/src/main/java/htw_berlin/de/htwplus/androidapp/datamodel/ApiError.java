package htw_berlin.de.htwplus.androidapp.datamodel;

/**
 * Represents an error of HTWPLUS RESTful API.
 *
 * @author Tino Herrmann, Tim Unkrig
 * @version 1.0
 */
public class ApiError {

    /** Title of the error. */
    private String mTitle;

    /** Message of the error. */
    private String mMessage;

    /** Code of the error. */
    private String mCode;

    /**
     * Creates a new api error with the given title, message and code.
     *
     * @param aTitle Title of error
     * @param aMessage Message of error
     * @param aCode Code of error
     *
     * @throws IllegalArgumentException Throws if title, message or code are null.
     */
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

    /**
     * Returns the error title.
     *
     * @return  Title of the error.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the error message.
     *
     * @return  Message of the error.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Returns the error code.
     *
     * @return  Code of the error.
     */
    public String getCode() {
        return mCode;
    }

    /**
     * Returns the complete error message.
     *
     * @return  Complete error message.
     */
    @Override
    public String toString() {
        return "[" + mCode + "] " + mTitle + ": " + mMessage;
    }
}
