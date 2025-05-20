package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.helpers.AliasFile;

public interface iFolloweesDao {

    public Integer getFolloweeCount(User follower);
    public AliasFile getFollowees(FollowingRequest request);
    public int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees);
}
