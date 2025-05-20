package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public interface iUserDao {

    public User getUser(GetItemSpec spec);
    public GetUserResponse findUser(GetUserRequest request);
    public List<User> findMultiUsers(List<String> alias);
    public User loginUser(LoginRequest request);
    public User registerUser(RegisterRequest request, String imageURL);
}
