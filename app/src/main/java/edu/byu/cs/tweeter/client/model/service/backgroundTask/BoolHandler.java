package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.BoolObserver;

public class BoolHandler extends BackgroundTaskHandler<BoolObserver>{
    public BoolHandler(BoolObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, BoolObserver observer) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}
