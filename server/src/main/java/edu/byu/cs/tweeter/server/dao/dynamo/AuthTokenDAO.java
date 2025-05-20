package edu.byu.cs.tweeter.server.dao.dynamo;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.iAuthTokenDao;

public class AuthTokenDAO implements iAuthTokenDao{

    private static final String tableName = "authtoken";
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);
    private static final String partitionKey = "token";
    //Sort key added in case needed later
    private static final String sortKey = "handle";
    private static final String KEY_DATE = "date";
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("-06:00");
    private static final long TIMEOUT = MILLISECONDS.convert(30, MINUTES);


    @Override
    public boolean isValidToken(AuthToken authToken) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey(partitionKey, authToken.token);
        try {
            Item outcome = table.getItem(spec);
            if (outcome != null) {
                long timeOfToken = outcome.getLong(KEY_DATE);
                LocalDateTime dateOfToken = LocalDateTime.ofEpochSecond(timeOfToken, 0, ZONE_OFFSET);
                LocalDateTime fromTime = LocalDateTime.from(dateOfToken);
                long minutesElapsed = fromTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
                if (minutesElapsed >= TIMEOUT) {
                    DeleteItemSpec deleteSpec = new DeleteItemSpec()
                            .withPrimaryKey(partitionKey, authToken);
                    table.deleteItem(deleteSpec);
                    return false;
                }
                else {
                    UpdateItemSpec updateSpec = new UpdateItemSpec().withPrimaryKey(partitionKey, authToken.token)
                            .withUpdateExpression("set date = :a")
                            .withValueMap(new ValueMap().withLong(":a", LocalDateTime.now().toInstant(ZONE_OFFSET).toEpochMilli()))
                            .withReturnValues(ReturnValue.UPDATED_OLD);
                    table.updateItem(updateSpec);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        try {
            DeleteItemSpec spec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey(partitionKey, request.getAuthToken().getToken(),
                            sortKey, request.getAlias()));
            table.deleteItem(spec);
            return new LogoutResponse();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new LogoutResponse("Error logging out.");
    }

    @Override
    public AuthToken createToken(String handle) {
        String token = UUID.randomUUID().toString();
        try {
            table.putItem(new Item().withString(partitionKey, token)
                    .withString(sortKey, handle)
                    .withLong(KEY_DATE, LocalDateTime.now().toInstant(ZONE_OFFSET).toEpochMilli()));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new AuthToken(token);
    }


}
