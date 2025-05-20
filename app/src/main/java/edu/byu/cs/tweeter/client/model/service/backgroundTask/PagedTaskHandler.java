package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Looper;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.Observer.PagedObserver;

public class PagedTaskHandler<T> extends BackgroundTaskHandler<PagedObserver> {

    public PagedTaskHandler(PagedObserver observer) {
        super(observer);

    }

    @Override
    protected void handleSuccess(Bundle data, PagedObserver observer) {
        List<T> items = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.handleSuccess(items, hasMorePages);
    }
}
