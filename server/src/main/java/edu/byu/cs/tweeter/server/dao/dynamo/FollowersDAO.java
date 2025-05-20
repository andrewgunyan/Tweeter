package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.iFollowersDao;
import edu.byu.cs.tweeter.server.helpers.AliasFile;
import edu.byu.cs.tweeter.util.FakeData;

public class FollowersDAO implements iFollowersDao {


    private static final String tableName = "follows";
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);
    private static final String partitionKey = "follower_handle";
    private static final String sortKey = "followee_handle";
    private static final String index = "follows_index";


    public Integer getFollowerCount(User follower) {
        // TODO: uses the dummy data.  Replace with a real implementation.
        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#followee", sortKey);
        //List<String> ans = new ArrayList<>();
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":handle", follower.getAlias());

        Index tableIndex = table.getIndex(index);
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#followee = :handle").withNameMap(nameMap)
                .withValueMap(valueMap)
                .withScanIndexForward(true);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        int i = 0;
        try {
            System.out.println("Followees:");
            items = tableIndex.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                i++;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return i;
    }


    public AliasFile getFollowersFirst(FollowersRequest request) {
        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#followee", sortKey);
        List<String> ans = new ArrayList<>();
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":handle", request.getFollowerAlias());


        Index tableIndex = table.getIndex(index);
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#followee = :handle").withNameMap(nameMap)
                .withValueMap(valueMap)
                .withScanIndexForward(true).withMaxResultSize(request.getLimit());

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            //System.out.println("Followees:");
            items = tableIndex.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                ans.add(item.getString(partitionKey));
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
        return new AliasFile(ans, more);
    }


    public AliasFile getFollowers(FollowersRequest request) {


        if (request.getLastFollowerAlias() == null) {
            return getFollowersFirst(request);
        }

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#followee", sortKey);
        List<String> ans = new ArrayList<>();
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":handle", request.getFollowerAlias());

        //GET FOLLOWERS

        PrimaryKey primKey = new PrimaryKey("follower_handle", request.getLastFollowerAlias())
                .addComponent("followee_handle", request.getFollowerAlias());


        Index tableIndex = table.getIndex(index);
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#followee = :handle").withNameMap(nameMap)
                .withValueMap(valueMap)
                .withScanIndexForward(true).withMaxResultSize(request.getLimit()).withExclusiveStartKey(primKey);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            //System.out.println("Followees:");
            items = tableIndex.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                ans.add(item.getString(partitionKey));
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
        return new AliasFile(ans, more);
    }








    public int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollowerAlias != null) {

            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    followersIndex = i + 1;
                }
            }
        }
        return followersIndex;
    }


}
