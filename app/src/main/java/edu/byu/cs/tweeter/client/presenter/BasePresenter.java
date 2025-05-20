package edu.byu.cs.tweeter.client.presenter;

public abstract class BasePresenter {

    protected BaseView view;
    public BasePresenter(BaseView view) {
        this.view = view;
    }

    public interface BaseView {
        void displayErrorMessage(String message);
    }

}
