package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FolloweesDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StoryDAO;
import edu.byu.cs.tweeter.server.dao.iAuthTokenDao;
import edu.byu.cs.tweeter.server.dao.iFeedDao;
import edu.byu.cs.tweeter.server.dao.iFollowersDao;
import edu.byu.cs.tweeter.server.dao.iStoryDao;
import edu.byu.cs.tweeter.server.dao.iUserDao;
import edu.byu.cs.tweeter.server.helpers.AliasFile;
import edu.byu.cs.tweeter.server.helpers.FeedFile;
import edu.byu.cs.tweeter.server.helpers.ParseStatus;
import edu.byu.cs.tweeter.server.helpers.UpdateFeedRequest;
import edu.byu.cs.tweeter.server.lambda.PostUpdateFeedMessages;
import edu.byu.cs.tweeter.server.lambda.UpdateFeed;

public class StatusService {

    private static final String queueURL = "https://sqs.us-west-2.amazonaws.com/025499393009/PostStatusQueue";
    private static final String feedURL = "https://sqs.us-west-2.amazonaws.com/025499393009/UpdateFeedQueue";

    iStoryDao storyDao;
    iFeedDao feedDao;
    iUserDao userDao;
    iAuthTokenDao authTokenDao;
    iFollowersDao followersDao;
    ParseStatus parseStatus;


    @Inject
    StatusService(iStoryDao storyDao, iFeedDao feedDao, iAuthTokenDao authTokenDao, iUserDao userDao, iFollowersDao followersDao) {
        this.storyDao = storyDao;
        this.feedDao = feedDao;
        this.authTokenDao = authTokenDao;
        this.userDao = userDao;
        this.followersDao = followersDao;
        parseStatus = new ParseStatus();
    }

    StatusService() {

    }

    public void Testing(iStoryDao storyDao, iUserDao userDao, ParseStatus parseStatus, iAuthTokenDao authTokenDao) {
        this.storyDao = storyDao;
        this.userDao = userDao;
        this.parseStatus = parseStatus;
        this.authTokenDao = authTokenDao;
    }


    public StoryResponse getStory(StoryRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new StoryResponse("Error");
        }
        List<Item> response =  storyDao.getStory(request);
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", request.getUserAlias());
        User user = userDao.getUser(spec);
        List<Status> result = parseStatus.getStory(response, user);

        return new StoryResponse(result, false);
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new FeedResponse("Error");
        }
        FeedFile feed = feedDao.getFeed(request);
        List<Item> response = feed.getItems();
        List<Status> result = parseStatus.getFeed(response);
        return new FeedResponse(result, feed.isHasMorePages());
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (!authTokenDao.isValidToken(request.getAuthToken())) {
            return new PostStatusResponse("Error");
        }


        PostStatusResponse response = storyDao.postStatus(request);

        if(response.isSuccess()) {
            Gson gson = new Gson();
            AmazonSQS queue = AmazonSQSClientBuilder.defaultClient();
            SendMessageRequest sendMessageRequest = new SendMessageRequest().withQueueUrl(queueURL)
                    .withMessageBody(gson.toJson(request));
            queue.sendMessage(sendMessageRequest);
        }





        return response;
    }

    public void UpdateFeedMessages(SQSEvent input, Context context) {

        //AliasFile file = followersDao.getFollowers(request);
        //List<User> users = userDao.findMultiUsers(file.getAliases());

        Gson gson = new Gson();
        AmazonSQS queue = AmazonSQSClientBuilder.defaultClient();
        for (SQSEvent.SQSMessage message : input.getRecords()) {
            PostStatusRequest request = gson.fromJson(message.getBody(), PostStatusRequest.class);
            String lastFollower = null;
            AliasFile file;
            do {
                file = followersDao.getFollowers(new FollowersRequest(request.getAuthToken(), request.getStatus().getUser().getAlias(), 10000, lastFollower));
                lastFollower = file.getAliases().get(file.getAliases().size()-1);
            } while (file.isHasMorePages());
            Status status = request.getStatus();
            List<String> users = file.getAliases();
            while (!users.isEmpty()) {
                List<String> usersTwentyFive = new ArrayList<>();
                for (int i = 0; i < 25; i++) {
                    if (users.isEmpty()) {
                        break;
                    }
                    usersTwentyFive.add(users.get(0));
                    users.remove(0);
                }
                UpdateFeedRequest feedRequest = new UpdateFeedRequest(usersTwentyFive, status);
                SendMessageRequest sendMessageRequest = new SendMessageRequest().withQueueUrl(feedURL)
                        .withMessageBody(gson.toJson(feedRequest));
                queue.sendMessage(sendMessageRequest);
            }
        }
    }

    public void UpdateFeedUsers(SQSEvent input, Context context) {

        //feedDao.PostStatusToFeed(request.getStatus(), users);
        Gson gson = new Gson();
        AmazonSQS queue = AmazonSQSClientBuilder.defaultClient();
        for (SQSEvent.SQSMessage message : input.getRecords()) {
            UpdateFeedRequest request = gson.fromJson(message.getBody(), UpdateFeedRequest.class);
            //List<User> users = userDao.findMultiUsers(request.getAliases());
            feedDao.PostStatusToFeed(request.getStatus(), request.getAliases());
        }
    }

    //StoryDAO storyDao {return new StoryDAO();}
    //FeedDAO getFeedDAO() {return new FeedDAO();}


}
