package edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer;

public interface ServiceObserver {
    void handleFailure(String message);
    void handleException(Exception ex);
}
