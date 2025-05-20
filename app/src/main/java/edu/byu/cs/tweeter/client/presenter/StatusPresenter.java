package edu.byu.cs.tweeter.client.presenter;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.Status;

public abstract class StatusPresenter extends PagedPresenter<Status>{

    private PagedView view;

    public PagedView getView() {
        return view;
    }
    protected StatusService statusService;

    public StatusPresenter(PagedView view) {
        super(view);
        statusService = new StatusService();
        this.view = view;
    }


    public class GetStatusObserver implements PagedObserver<Status> {
        String input;
        GetStatusObserver(String input) {this.input = input;}

        @Override
        public void handleSuccess(List<Status> statuses, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingStatus(false);
            last = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addItem(statuses);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingStatus(false);

            view.displayErrorMessage("Failed to get " + input + ": " + message);

        }

        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.setLoadingStatus(false);

            view.displayErrorMessage("Failed to get " + input  + " because of exception: " + exception.getMessage());

        }
    }

}
