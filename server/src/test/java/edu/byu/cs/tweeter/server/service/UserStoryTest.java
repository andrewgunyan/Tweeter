package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FolloweesDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.UserDAO;
import edu.byu.cs.tweeter.server.helpers.ParseStatus;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.StatusService;

public class UserStoryTest {

    private StoryRequest request;
    private StoryResponse expectedResponse;
    private StoryDAO mockStoryDAO;
    private UserDAO mockUserDAO;
    private AuthTokenDAO mockAuthDAO;
    private StatusService statusServiceSpy;
    private ParseStatus mockParse;

    @Before
    public void setup() {
        AuthToken authToken = new AuthToken();

        User resultUser1 = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");

        Status resultStatus1 = new Status("post1", resultUser1, "date", new ArrayList<>(), new ArrayList<>());
        Status resultStatus2 = new Status("post2", resultUser1, "date", new ArrayList<>(), new ArrayList<>());
        Status resultStatus3 = new Status("post3", resultUser1, "date", new ArrayList<>(), new ArrayList<>());

        List<Status> result = new ArrayList<>();
        result.add(resultStatus1);
        result.add(resultStatus2);
        result.add(resultStatus3);
        List<Item> placeHolder = new ArrayList<>();

        request = new StoryRequest(authToken, resultUser1.getAlias(), 3, null);
        mockParse = Mockito.mock(ParseStatus.class);
        mockUserDAO = Mockito.mock(UserDAO.class);
        mockAuthDAO = Mockito.mock(AuthTokenDAO.class);

        expectedResponse = new StoryResponse(Arrays.asList(resultStatus1, resultStatus2, resultStatus3), false);
        mockStoryDAO = Mockito.mock(StoryDAO.class);
        Mockito.when(mockStoryDAO.getStory(request)).thenReturn(placeHolder);
        Mockito.when(mockUserDAO.getUser(Mockito.any())).thenReturn(resultUser1);
        Mockito.when(mockParse.getStory(Mockito.any(), Mockito.any())).thenReturn(result);
        Mockito.when(mockAuthDAO.isValidToken(authToken)).thenReturn(true);

        statusServiceSpy = Mockito.spy(StatusService.class);
        statusServiceSpy.Testing(mockStoryDAO, mockUserDAO, mockParse, mockAuthDAO);
    }

    @Test
    public void testGetSuccess() {
        StoryResponse response = statusServiceSpy.getStory(request);
        Assert.assertEquals(expectedResponse.getPosts(), response.getPosts());
        Assert.assertEquals(expectedResponse.getHasMorePages(), response.getHasMorePages());
    }

}