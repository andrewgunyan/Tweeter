package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.GetUserObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends BasePresenter{

    protected UserService userService;

    protected static final int PAGE_SIZE = 10;

    protected T last;

    protected boolean hasMorePages;

    protected PagedView pagedView;

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    protected boolean isLoading = false;

    public PagedPresenter(PagedView view) {
        super(view);
        pagedView = view;
        userService = new UserService();
    }

    public UserService getUserService() {
        return userService;
    }


    public void loadUser(String userAlias) {

        userService.loadUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new UserObserver());

    }

    public PagedView getPagedView() {
        return pagedView;
    }


    public interface PagedView<U> extends BaseView {
        void displayUserToast(String message);
        void HandleIntent(User user);
        void setLoadingStatus(boolean value);
        void addItem(List<U> items);
    }


    public class UserObserver implements GetUserObserver {

        @Override
        public void handleSuccess(User user) {
            pagedView.HandleIntent(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage("Failed to get user's profile because of exception: " + exception.getMessage());
        }

        @Override
        public void ToastProfile() {
            pagedView.displayUserToast("Getting user's profile...");
        }

    }


}
