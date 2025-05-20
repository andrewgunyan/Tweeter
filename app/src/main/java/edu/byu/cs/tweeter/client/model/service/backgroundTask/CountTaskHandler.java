package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.CountObserver;

public class CountTaskHandler extends BackgroundTaskHandler<CountObserver> {


    public CountTaskHandler(CountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, CountObserver observer) {
        observer.handleSuccess(data.getInt(CountTask.COUNT_KEY));
    }
}
