package edu.byu.cs.tweeter.client.presenter;



public class LoginPresenter extends AuthenticatePresenter {


    public LoginPresenter(AuthView view) {
        super(view);
    }

    public void login(String alias, String password) {
        getLoginPageService().getLogin(alias, password, new LoginAndRegister("login"));
    }
















}
