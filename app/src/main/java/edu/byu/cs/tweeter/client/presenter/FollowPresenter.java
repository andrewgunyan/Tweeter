package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowPresenter extends PagedPresenter<User>{

    protected PagedView view;
    public PagedView getView() {
        return view;
    }
    protected FollowService followService;

    public FollowPresenter(PagedView view) {
        super(view);
        followService = new FollowService();
        this.view = view;
    }

    public class GetFollowsObserver implements PagedObserver<User> {
        String input;
        GetFollowsObserver(String input) {this.input = input;}

        @Override
        public void handleSuccess(List<User> followees, boolean hasMorePages) {
            isLoading = false;
            getView().setLoadingStatus(false);
            last = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
            setHasMorePages(hasMorePages);
            getView().addItem(followees);

        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingStatus(false);

            view.displayErrorMessage("Failed to get "+ input +": " + message);
        }

        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.setLoadingStatus(false);

            view.displayErrorMessage("Failed to get "+ input + " because of exception: " + exception.getMessage());
        }
    }

}
