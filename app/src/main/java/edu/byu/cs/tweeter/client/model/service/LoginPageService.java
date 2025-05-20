package edu.byu.cs.tweeter.client.model.service;

import static edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskExecute.runTask;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.AuthenticateHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.UserObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.SimpleNotificationHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class LoginPageService {

    private static final String URL_PATH = "/login";

    private ServerFacade serverFacade;

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    public void logout(AuthToken currUserAuthToken, SimpleNotificationObserver logoutObserver) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new SimpleNotificationHandler(logoutObserver));
        runTask(logoutTask);
    }

    public void getRegister(String firstName, String lastName, String alias, String password, String imageBytesBase64, UserObserver getRegisterObserver ) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new AuthenticateHandler(getRegisterObserver));
        System.out.println("here in service client");
        runTask(registerTask);
    }


    public void getLogin(String alias, String password, UserObserver getLoginObserver) {
        LoginTask loginTask = new LoginTask(alias,
                password,
                new AuthenticateHandler(getLoginObserver));
        runTask(loginTask);
    }

}
