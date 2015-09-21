package htw_berlin.de.htwplus.androidapp.datamodel;

/**
 * Created by tino on 28.06.15.
 */
public class ApiError {

    private String title;
    private String message;
    private String code;

    public ApiError(String aTitle, String aMessage, String aCode) {
        boolean inputOk = ((aTitle != null) && (aMessage != null) &&
                          (aCode != null));
        if(inputOk)
        {
            title = aTitle;
            message = aMessage;
            code = aCode;
        }
        else
            throw new IllegalArgumentException("Invalid arguments for apierror initialization.");
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "[" + code + "] " + title + ": " + message;
    }
}
