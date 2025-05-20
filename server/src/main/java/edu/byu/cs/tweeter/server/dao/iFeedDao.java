package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.helpers.FeedFile;
import edu.byu.cs.tweeter.server.helpers.UpdateFeedRequest;

public interface iFeedDao {

    //public Integer getFeedCount(User user);
    public FeedFile getFeed(FeedRequest request);
    public FeedFile getFeedFirst(FeedRequest request);
    //public int getPostsStartingIndex(Status lastStoryStatus, List<Status> allPosts);
    public void PostStatusToFeed(Status status, List<String> users);
    public void UpdateFeeds(UpdateFeedRequest request);
}
