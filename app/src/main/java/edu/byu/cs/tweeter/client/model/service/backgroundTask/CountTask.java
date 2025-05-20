package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class CountTask extends AuthenticatedTask{

    public static final String COUNT_KEY = "count";


    /**
     * Auth token for logged-in user.
     */

    /**
     * The user whose follower count is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    protected User currUser;

    protected int count;

    public CountTask(Handler messageHandler, AuthToken authToken, User currUser) {
        super(messageHandler, authToken);
        this.currUser = currUser;
    }


    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putInt(COUNT_KEY, count);
    }
}
