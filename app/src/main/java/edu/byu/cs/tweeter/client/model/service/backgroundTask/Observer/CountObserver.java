package edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer;

public interface CountObserver extends ServiceObserver{
    void handleSuccess(int count);
}
