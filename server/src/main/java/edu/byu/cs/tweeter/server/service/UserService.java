package edu.byu.cs.tweeter.server.service;

import com.google.inject.Inject;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.iAuthTokenDao;
import edu.byu.cs.tweeter.server.dao.iFeedDao;
import edu.byu.cs.tweeter.server.dao.iStoryDao;
import edu.byu.cs.tweeter.server.dao.iUserDao;
import edu.byu.cs.tweeter.server.helpers.S3FakeDao;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    iUserDao userDao;
    iAuthTokenDao authTokenDao;

    @Inject
    UserService( iUserDao userDao, iAuthTokenDao authTokenDao) {
        this.userDao = userDao;
        this.authTokenDao = authTokenDao;
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        User user = userDao.loginUser(request);
        if (user == null) {
            return new LoginResponse("Error");
        }
        //System.out.println(user.getAlias());
        AuthToken authToken = authTokenDao.createToken(user.getAlias());
        //System.out.println(authToken.getToken());
        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        } else if(request.getFirstname() == null) {
            throw new RuntimeException("[BadRequest] Missing a firstname");
        } else if(request.getLastname() == null) {
            throw new RuntimeException("[BadRequest] Missing a lastname");
        } else if(request.getImage() == null) {
            throw new RuntimeException("[BadRequest] Missing a image");
        }
        S3FakeDao s3 = new S3FakeDao();
        String image = s3.createPicture(request);
        User user = userDao.registerUser(request, image);
        AuthToken authToken = authTokenDao.createToken(user.getAlias());
        //System.out.println(user.getAlias() + "Service");
        return new RegisterResponse(user, authToken);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[BadRequest] Missing a user alias");
        }
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            throw new RuntimeException("AuthToken Expired");
        }
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new GetUserResponse("Error");
        }
        return userDao.findUser(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new LogoutResponse("Error");
        }
        return authTokenDao.logout(request);
    }



}
