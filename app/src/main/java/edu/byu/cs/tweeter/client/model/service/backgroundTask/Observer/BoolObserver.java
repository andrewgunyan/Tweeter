package edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer;

public interface BoolObserver extends ServiceObserver{
    void handleSuccess(boolean success);
}
