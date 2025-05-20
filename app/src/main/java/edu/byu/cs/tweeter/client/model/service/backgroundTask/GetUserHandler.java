package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.GetUserObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserHandler extends BackgroundTaskHandler<UserObserver> {

    public GetUserHandler(GetUserObserver observer) {
        super(observer);
        observer.ToastProfile();
    }

    @Override
    protected void handleSuccess(Bundle data, UserObserver observer) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);

        observer.handleSuccess(user);
    }
}
