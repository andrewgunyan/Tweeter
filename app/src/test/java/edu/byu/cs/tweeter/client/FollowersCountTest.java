package edu.byu.cs.tweeter.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class FollowersCountTest {

    private FollowersCountRequest request;
    private FollowersCountResponse expectedResponse;
    private ServerFacade facade = new ServerFacade();

    @Before
    public void setup() {
        AuthToken authToken = new AuthToken();

        // Setup a request object to use in the tests
        request = new FollowersCountRequest(authToken, new FakeData().getFirstUser());

        // Setup a mock FollowersDAO that will return known responses
        expectedResponse = new FollowersCountResponse(21);

    }

    /**
     * Verify that the {@link FollowService#getFollowers(FollowersRequest)}
     * method returns the same result as the {@link FollowersDAO} class.
     */
    @Test
    public void testGetFollowersCount() throws IOException, TweeterRemoteException {
        FollowersCountResponse response = facade.getFollowersCount(request, "/getfollowerscount");
        Assert.assertEquals(expectedResponse.getCount(), response.getCount());
    }
}
