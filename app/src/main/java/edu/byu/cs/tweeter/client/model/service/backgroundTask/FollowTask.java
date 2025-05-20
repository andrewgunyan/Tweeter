package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "FollowTask";
    static final String URL_PATH = "/follow";


    /**
     * The user that is being followed.
     */
    private User followee;
    private User user;
    /**
     * Message handler that will receive task results.
     */


    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(messageHandler, authToken);
        this.followee = followee;
        this.user = Cache.getInstance().getCurrUser();
    }


    @Override
    protected void proccessTask() {
        try {

            User targetUser = followee == null ? null : followee;
            User currUser = user == null ? null : user;


            FollowRequest request = new FollowRequest(authToken, targetUser, currUser);
            FollowResponse response = getServerFacade().follow(request, URL_PATH);

            if(response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to follow", ex);
            sendExceptionMessage(ex);
        }
    }


    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {

    }


}
