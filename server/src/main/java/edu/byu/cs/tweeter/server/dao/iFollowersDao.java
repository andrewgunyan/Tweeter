package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.server.helpers.AliasFile;

public interface iFollowersDao {

    public Integer getFollowerCount(User follower);
    public AliasFile getFollowers(FollowersRequest request);
    public AliasFile getFollowersFirst(FollowersRequest request);
    public int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers);
}
