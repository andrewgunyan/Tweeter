package edu.byu.cs.tweeter.client.presenter;



import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.LoginPageService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.BoolObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends BasePresenter {

    private StatusService statusService;
    private FollowService followService;
    private LoginPageService loginPageService;
    private PostStatusObserver postStatusObserver;
    private AuthToken authToken;

    public MainPresenter(View view) {
        super(view);
        followService = new FollowService();
    }

    protected StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

    protected LoginPageService getLoginPageService() {
        if (loginPageService == null) {
            loginPageService = new LoginPageService();
        }
        return loginPageService;
    }

    public SimpleNotificationObserver getPostStatusObserver() {
        if (postStatusObserver == null) {
            postStatusObserver = new PostStatusObserver();
        }
        return postStatusObserver;
    }

    public AuthToken getAuthToken() {
        if (authToken == null) {
            authToken = Cache.getInstance().getCurrUserAuthToken();
        }
        return authToken;
    }

    public interface View extends BaseView{
        void logOut();
        void logOutToast();
        void postStatusToast();
        void postMessage();
        void IsFollower(boolean follower);
        void followToast(String toast);
        void EnableFollow();
        void Followed(Boolean follow);
        void Followers(int count);
        void Following(int count);
    }

    public void IsFollower(User selectedUser) {

        followService.isFollower(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerObserver());

    }

    public void follow(User selectedUser) {
        followService.follow(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowObserver("Adding", "follow", false));


    }

    public void unFollow(User selectedUser) {

        followService.unFollow(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowObserver("Removing", "unfollow", true));

    }

    public void logout() {
        getView().logOutToast();
        getLoginPageService().logout(Cache.getInstance().getCurrUserAuthToken(),
                new LogoutObserver());
    }

    public void postStatus(Status newStatus) {
        getView().postStatusToast();
        getStatusService().postStatus(getAuthToken(),
                newStatus, getPostStatusObserver());
    }

    public void getFollowerCount(User selectedUser) {
        followService.getFollowerCount(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowerCountObserver());

    }

    public void getFollowingCount(User selectedUser) {
        followService.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowingCountObserver());
    }

    public View getView() {
        return (View)this.view;
    }


    public class GetFollowingCountObserver implements CountObserver {

        @Override
        public void handleSuccess(int count) {
            getView().Following(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to get following count: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to get following count because of exception: " + exception.getMessage());
        }
    }

    public class GetFollowerCountObserver implements CountObserver {

        @Override
        public void handleSuccess(int count) {
            getView().Followers(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to get followers count: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to get followers count because of exception: " + exception.getMessage());
        }
    }

    public class PostStatusObserver implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            getView().postMessage();
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to post status: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to post status because of exception: " + exception.getMessage());
        }
    }

    public class LogoutObserver implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            Cache.getInstance().clearCache();
            getView().logOut();
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to logout: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to logout because of exception: " + exception.getMessage());
        }
    }

    public class FollowObserver implements SimpleNotificationObserver {

        String toast;
        String type;
        Boolean follow;
        FollowObserver(String toast, String type, Boolean bool) {
            this.toast = toast;
            this.type = type;
            this.follow = bool;
        }

        @Override
        public void handleSuccess() {
            getView().followToast(toast);
            getView().EnableFollow();

            getView().Followed(follow);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to " + type + ": " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to " + type + " because of exception: " + exception.getMessage());
        }
    }


    public class IsFollowerObserver implements BoolObserver {

        @Override
        public void handleSuccess(boolean isFollower) {
            getView().IsFollower(isFollower);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to determine following relationship because of exception: " + exception.getMessage());
        }
    }

}
