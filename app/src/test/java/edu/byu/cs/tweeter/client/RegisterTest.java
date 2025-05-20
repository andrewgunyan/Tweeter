package edu.byu.cs.tweeter.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class RegisterTest {
    private RegisterRequest request;
    private RegisterResponse expectedResponse;
    private ServerFacade facade = new ServerFacade();


    @Before
    public void setup() {


        String username = "Username";
        String password = "Password";
        String firstname = "Firstname";
        String lastname = "Lastname";
        String image = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png";

        User user = new FakeData().getFirstUser();
        AuthToken authToken = new FakeData().getAuthToken();

        // Setup a request object to use in the tests
        request = new RegisterRequest(username, password, firstname, lastname, image);
        // Setup a mock FollowDAO that will return known responses
        expectedResponse = new RegisterResponse(user, authToken);

    }

    /**
     * Verify that the {@link FollowService#getFollowees(FollowingRequest)}
     * method returns the same result as the {@link FollowDAO} class.
     */
    @Test
    public void testRegister() throws IOException, TweeterRemoteException {
        RegisterResponse response = facade.register(request, "/register");
        Assert.assertEquals(expectedResponse.getUser(), response.getUser());
    }
}
