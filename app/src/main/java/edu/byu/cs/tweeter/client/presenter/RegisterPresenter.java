package edu.byu.cs.tweeter.client.presenter;

public class RegisterPresenter extends AuthenticatePresenter {


    public RegisterPresenter(AuthView view) {
        super(view);
    }

    public void register(String firstName, String lastName, String alias, String password, String image) {
        getLoginPageService().getRegister(firstName, lastName, alias, password, image, new LoginAndRegister("register"));
    }



}
