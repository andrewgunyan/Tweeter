package edu.byu.cs.tweeter.client.model.service;

import static edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskExecute.runTask;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BoolHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.CountTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.BoolObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {




    /**
     * Returns an instance of {@link ServerFacade}. Allows mocking of the ServerFacade class for
     * testing purposes. All usages of ServerFacade should get their instance from this method to
     * allow for proper mocking.
     *
     * @return the instance.
     */


    public void getFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserver<User> getFollowersObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new PagedTaskHandler<User>(getFollowersObserver));
        runTask(getFollowersTask);
    }

    public void getFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> getFollowingObserver) {

        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedTaskHandler<User>(getFollowingObserver));

        runTask(getFollowingTask);
    }

    public void isFollower(AuthToken currUserAuthToken, User user, User selectedUser, BoolObserver isFollowerObserver) {

        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken,
                user, selectedUser, new BoolHandler(isFollowerObserver));
        runTask(isFollowerTask);

    }

    public void unFollow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(unfollowObserver));
        runTask(unfollowTask);
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(followObserver));
        runTask(followTask);

    }

    public void getFollowerCount(AuthToken currUserAuthToken, User selectedUser, CountObserver getFollowerCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken,
                selectedUser, new CountTaskHandler(getFollowerCountObserver));
        runTask(followersCountTask);
    }

    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, CountObserver getFollowingCountObserver) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new CountTaskHandler(getFollowingCountObserver));
        runTask(followingCountTask);
    }

}
