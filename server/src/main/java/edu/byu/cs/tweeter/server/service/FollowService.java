package edu.byu.cs.tweeter.server.service;

import com.google.inject.Inject;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.iAuthTokenDao;
import edu.byu.cs.tweeter.server.dao.iFollowDao;
import edu.byu.cs.tweeter.server.dao.iFolloweesDao;
import edu.byu.cs.tweeter.server.dao.iFollowersDao;
import edu.byu.cs.tweeter.server.dao.iUserDao;
import edu.byu.cs.tweeter.server.helpers.AliasFile;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    iFolloweesDao followeeDao;
    iFollowersDao followersDao;
    iFollowDao followDao;
    iUserDao userDao;
    iAuthTokenDao authTokenDao;

    @Inject
    FollowService(iFolloweesDao followeeDao, iFollowersDao followersDao, iUserDao userDao, iAuthTokenDao authTokenDao, iFollowDao followDao) {
        this.followeeDao = followeeDao;
        this.followersDao = followersDao;
        this.userDao = userDao;
        this.authTokenDao = authTokenDao;
        this.followDao = followDao;
    }


    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new FollowingResponse("Error");
        }
        AliasFile file = followeeDao.getFollowees(request);
        return new FollowingResponse(userDao.findMultiUsers(file.getAliases()), file.isHasMorePages());
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }

        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new FollowersResponse("Error");
        }
        AliasFile file = followersDao.getFollowers(request);

        return new FollowersResponse(userDao.findMultiUsers(file.getAliases()),file.isHasMorePages());
    }

    public FollowResponse follow(FollowRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        } else if(request.getCurrUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a current user");
        }

        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new FollowResponse("Error");
        }
        return followDao.addFollow(request);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        } else if(request.getCurrUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a current user");
        }
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new UnfollowResponse("Error");
        }
        return followDao.deleteFollow(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user");
        } else if(request.getCurrUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a current user");
        }
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new IsFollowerResponse("Error");
        }
        return followDao.checkFollow(request);
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        if(request.getCurrUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        }
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new FollowersCountResponse("Error");
        }
        return new FollowersCountResponse(followersDao.getFollowerCount(request.getCurrUser()));
    }
    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        if(request.getCurrUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        }
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new FollowingCountResponse("Error");
        }
        return new FollowingCountResponse(followeeDao.getFolloweeCount(request.getCurrUser()));
    }



}
