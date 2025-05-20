package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask {
    private static final String LOG_TAG = "RegisterTask";
    static final String URL_PATH = "/register";

    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;

    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private String image;


    public RegisterTask(String firstName, String lastName, String username, String password,
                        String image, Handler messageHandler) {
        super(messageHandler, username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;

    }

    @Override
    protected void proccessTask() {
        try {
            String targetUsername = getUsername() == null ? null : getUsername();
            String targetPassword = getPassword() == null ? null : getPassword();
            String targetFirstName = firstName == null ? null : firstName;
            String targetLastName = lastName == null ? null : lastName;
            String targetImage = image == null ? null : image;


            RegisterRequest request = new RegisterRequest(targetUsername, targetPassword, targetFirstName, targetLastName, targetImage);
            RegisterResponse response = getServerFacade().register(request, URL_PATH);

            if(response.isSuccess()) {
                user = response.getUser();
                auth = response.getAuthToken();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage() + " HELLO");
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to login", ex);
            sendExceptionMessage(ex);
        }
    }
}
