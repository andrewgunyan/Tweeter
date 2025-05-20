package edu.byu.cs.tweeter.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class FollowersTest {
    private FollowersRequest request;
    private FollowersResponse expectedResponse;
    private ServerFacade facade = new ServerFacade();

    @Before
    public void setup() {
        AuthToken authToken = new AuthToken();


        // Setup a request object to use in the tests
        request = new FollowersRequest(authToken, new FakeData().getFirstUser().getAlias(), 21, null);

        // Setup a mock FollowersDAO that will return known responses
        //expectedResponse = new FollowersResponse(new FakeData().getFakeUsers(), false);

    }


    @Test
    public void testGetFollowersSuccess() throws IOException, TweeterRemoteException {
        FollowersResponse response = facade.getFollowers(request, "/getfollowers");
        Assert.assertEquals(expectedResponse.getFollowers(), response.getFollowers());
    }


}
