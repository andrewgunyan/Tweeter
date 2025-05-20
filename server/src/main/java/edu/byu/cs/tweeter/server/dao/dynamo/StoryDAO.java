package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.iStoryDao;
import edu.byu.cs.tweeter.util.FakeData;

public class StoryDAO implements iStoryDao {

    private static final String tableName = "story";
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);
    private static final String partitionKey = "handle";
    private static final String sortKey = "statusDate";
    private static final String STATUS_KEY = "status";

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param user the User whose count of how many following is desired.
     * @return said count.
     */
   /* public Integer getStoryCount(User user) {
        // TODO: uses the dummy data.  Replace with a real implementation.
        assert user != null;
        return getDummyStory().size();
    }*/

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public List<Item> getStory(StoryRequest request) {
        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#user", partitionKey);
        List<Item> ans = new ArrayList<>();

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", request.getUserAlias());

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#user = :handle")
                .withNameMap(nameMap)
                .withValueMap(valueMap)
                .withScanIndexForward(false);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            //System.out.println("Followees:");
            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                ans.add(item);
            }
            return ans;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            Status status = request.getStatus();
            //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "scary");
            table.putItem(new Item().withPrimaryKey(partitionKey, status.getUser().getAlias(), sortKey, status.getDate())
                    .withString("post", status.getPost()));
            return new PostStatusResponse(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "extra scary");
        return new PostStatusResponse("Error posting status.");
    }

}
