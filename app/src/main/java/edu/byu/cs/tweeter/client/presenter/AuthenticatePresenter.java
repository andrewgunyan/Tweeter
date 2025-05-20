package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.LoginPageService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticatePresenter extends BasePresenter {

    private LoginPageService loginPageService;
    AuthView authView;

    public AuthenticatePresenter(AuthView view) {
        super(view);
        authView = view;
        loginPageService = new LoginPageService();
    }

    public LoginPageService getLoginPageService() {
        return loginPageService;
    }


    public interface AuthView extends BaseView{
        void HandleIntent(User user);
    }

    public class LoginAndRegister implements UserObserver {
        String input;

        LoginAndRegister(String input) {
            this.input = input;
        }

        @Override
        public void handleSuccess(User user) {
            authView.HandleIntent(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to " + input + ": " + message);

        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to " + input + " because of exception: " + exception.getMessage());
        }
    }








}
