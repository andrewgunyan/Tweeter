package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.Item;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public interface iStoryDao {

    //public Integer getStoryCount(User user);
    public List<Item> getStory(StoryRequest request);
    //public int getPostsStartingIndex(Status lastStoryStatus, List<Status> allPosts);
    public PostStatusResponse postStatus(PostStatusRequest request);
}
