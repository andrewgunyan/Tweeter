package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.iFeedDao;
import edu.byu.cs.tweeter.server.helpers.AliasFile;
import edu.byu.cs.tweeter.server.helpers.FeedFile;
import edu.byu.cs.tweeter.server.helpers.UpdateFeedRequest;
import edu.byu.cs.tweeter.util.FakeData;

public class FeedDAO implements iFeedDao {

    private static final String tableName = "feed";
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);
    private static final String partitionKey = "handle";
    private static final String sortKey = "statusDate";
    //private static final String STATUS_KEY = "status";



    /*public Integer getFeedCount(User user) {
        // TODO: uses the dummy data.  Replace with a real implementation.
        assert user != null;
        return getDummyFeed().size();
    }*/

    public FeedFile getFeedFirst(FeedRequest request) {

        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#user", partitionKey);
        List<Item> ans = new ArrayList<>();

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", request.getUserAlias());


        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#user = :handle").withNameMap(nameMap)
                .withValueMap(valueMap)
                .withScanIndexForward(false).withMaxResultSize(request.getLimit());

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
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assert items != null;
        QueryOutcome last = items.getLastLowLevelResult();
        QueryResult result = last.getQueryResult();
        boolean more = false;
        if (result.getLastEvaluatedKey() != null) {
            more = true;
        }
        //assert iterator != null;
        return new FeedFile(ans, more);
    }


    public FeedFile getFeed(FeedRequest request) {
        /*HashMap<String, String> nameMap = new HashMap<>();
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
        return null;*/
        if (request.getLastFeedStatus() == null) {
            return getFeedFirst(request);
        }

        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#user", partitionKey);
        List<Item> ans = new ArrayList<>();

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":handle", request.getUserAlias());

        //GET FOLLOWERS

        PrimaryKey primKey = new PrimaryKey("handle", request.getUserAlias())
                .addComponent("statusDate", request.getLastFeedStatus().getDate());


        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#user = :handle").withNameMap(nameMap)
                .withValueMap(valueMap)
                .withScanIndexForward(false).withMaxResultSize(request.getLimit()).withExclusiveStartKey(primKey);

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
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assert items != null;
        QueryOutcome last = items.getLastLowLevelResult();
        QueryResult result = last.getQueryResult();
        boolean more = false;
        if (result.getLastEvaluatedKey() != null) {
            more = true;
        }
        //assert iterator != null;
        return new FeedFile(ans, more);
    }


    public void UpdateFeeds(UpdateFeedRequest request) {

    }


    public void PostStatusToFeed(Status status, List<String> users) {
        Gson gson = new Gson();
        //Change to batch write
        List<Item> requests = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            requests.add(new Item().withPrimaryKey(partitionKey, users.get(i), sortKey, status.getDate())
                    .withString("post", status.getPost())
                    .withString("poster", status.getUser().getAlias()));
        }

        TableWriteItems tableWriteItems = new TableWriteItems(tableName).withItemsToPut(requests);
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(tableWriteItems);

        do {
            // ensure no items left behind #Obama
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            if (unprocessedItems.size() > 0) {
                outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            }
        } while (outcome.getUnprocessedItems().size() > 0);
    }
}
