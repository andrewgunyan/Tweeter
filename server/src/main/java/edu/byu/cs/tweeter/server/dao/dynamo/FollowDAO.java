package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.iFollowDao;

public class FollowDAO implements iFollowDao {

    private static final String tableName = "follows";
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);
    private static final String partitionKey = "follower_handle";
    private static final String sortKey = "followee_handle";
    private static final String index = "follows_index";

    @Override
    public FollowResponse addFollow(FollowRequest request) {
        try {
            table.putItem(new Item().withString(partitionKey, request.getCurrUser().getAlias())
                    .withString("follower_name", request.getCurrUser().getName())
                    .withString(sortKey, request.getTargetUser().getAlias())
                    .withString("followee_name", request.getTargetUser().getName()));
            return new FollowResponse();
        }
        catch (Exception e) {
            System.err.println("Unable to add item");
            System.err.println(e.getMessage());
        }
        return new FollowResponse("Error following " + request.getTargetUser().getAlias() + ".");
    }

    @Override
    public UnfollowResponse deleteFollow(UnfollowRequest request) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(partitionKey, request.getCurrUser().getAlias(), sortKey, request.getTargetUser().getAlias()));

        try {
            table.deleteItem(deleteItemSpec);
            return new UnfollowResponse();
        }
        catch (Exception e) {
            System.err.println("Unable to delete item");
            System.err.println(e.getMessage());
        }
        return new UnfollowResponse("Error unfollowing " + request.getTargetUser().getAlias() + ".");
    }

    @Override
    public IsFollowerResponse checkFollow(IsFollowerRequest request) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey(partitionKey, request.getCurrUser().getAlias()
                , sortKey, request.getTargetUser().getAlias());
        try {
            table.getItem(spec);
            if (table.getItem(spec) != null) {
                return new IsFollowerResponse(true);
            }
            else {
                return new IsFollowerResponse(false);
            }
        }
        catch (Exception e) {
            return new IsFollowerResponse("Not a follower");
        }
    }
}
