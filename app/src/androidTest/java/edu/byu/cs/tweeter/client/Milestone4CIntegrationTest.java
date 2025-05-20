package edu.byu.cs.tweeter.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.LoginPageService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter.View;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class Milestone4CIntegrationTest {

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private MainPresenter mainPresenter;
    private LoginPageService loginSpy;
    private StatusService statusSpy;
    private View mockView;
    private CountDownLatch countDownLatch;
    private ServerFacade facade = new ServerFacade();
    private Status resultStatus1;
    private User resultUser1;
    private StatusService statusService;
    private StoryRequest storyRequest;
    private List<Status> expected = new ArrayList<>();

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }


    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    public class MockObserver implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            mockView.postMessage();
            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
        }

        @Override
        public void handleException(Exception exception) {
        }
    }



    @Before
    public void setup() {

        loginRequest = new LoginRequest("@1", "1");
        mockView = Mockito.mock(View.class);
        mainPresenter = Mockito.spy(new MainPresenter(mockView));
        Mockito.when(mainPresenter.getPostStatusObserver()).thenReturn(new MockObserver());
        resetCountDownLatch();
    }


    @Test
    public void verifyAll() throws InterruptedException, IOException, TweeterRemoteException, ParseException {

        LoginResponse response = facade.login(loginRequest, "/login");
        User user = response.getUser();
        Assert.assertEquals("@1", user.getAlias());
        resultStatus1 = new Status("post1", response.getUser(), getFormattedDateTime(), new ArrayList<>(), new ArrayList<>());
        Mockito.when(mainPresenter.getAuthToken()).thenReturn(response.getAuthToken());
        mainPresenter.postStatus(resultStatus1);
        awaitCountDownLatch();
        Mockito.verify(mockView).postMessage();


        expected.add(resultStatus1);
        //TODO should be working, but need to fix post first
        storyRequest = new StoryRequest(response.getAuthToken(), "@1", 10, null);
        StoryResponse storyResponse = facade.getStory(storyRequest, "/getstory");
        Assert.assertEquals(expected, storyResponse.getPosts());


    }
}
