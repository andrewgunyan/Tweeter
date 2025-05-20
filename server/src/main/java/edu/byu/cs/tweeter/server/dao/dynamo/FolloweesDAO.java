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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.iFolloweesDao;
import edu.byu.cs.tweeter.server.helpers.AliasFile;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FolloweesDAO implements iFolloweesDao {

    private static final String tableName = "follows";
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);
    private static final String partitionKey = "follower_handle";
    private static final String sortKey = "followee_handle";
    private static final String index = "follows_index";

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param follower the User whose count of how many following is desired.
     * @return said count.
     */
    public Integer getFolloweeCount(User follower) {
        // TODO: uses the dummy data.  Replace with a real implementation.
        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#follower", partitionKey);
        //List<String> ans = new ArrayList<>();
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":handle", follower.getAlias());

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#follower = :handle").withNameMap(nameMap)
                .withValueMap(valueMap)
                .withScanIndexForward(true);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        int i = 0;
        try {
            System.out.println("Followees:");
            items = table.query(querySpec);

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



    public AliasFile getFollowees(FollowingRequest request) {
        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#follower", partitionKey);
        List<String> ans = new ArrayList<>();
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":handle", request.getFollowerAlias());

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#follower = :handle").withNameMap(nameMap)
                .withValueMap(valueMap)
                .withScanIndexForward(true);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            System.out.println("Followees:");
            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                ans.add(item.getString(sortKey));
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //assert iterator != null;
        return new AliasFile(ans, false);
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    public int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }




}
