package edu.byu.cs.tweeter.server.helpers;

import com.amazonaws.services.dynamodbv2.document.Item;

import java.util.List;

public class FeedFile {

    public FeedFile(List<Item> items, boolean hasMorePages) {
        this.items = items;
        this.hasMorePages = hasMorePages;
    }

    private List<Item> items;
    private boolean hasMorePages;


    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }
}
