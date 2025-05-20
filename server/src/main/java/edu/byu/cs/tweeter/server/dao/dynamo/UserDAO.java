package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.iUserDao;
import edu.byu.cs.tweeter.server.helpers.Hashing;

public class UserDAO implements iUserDao {

    private static final String tableName = "users";
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);
    private static final String partitionKey = "alias";

    //TODO make new class for s3


    @Override
    public User getUser(GetItemSpec spec) {
        Item outcome = table.getItem(spec);
        User user = new User();
        user.setAlias(outcome.getString(partitionKey));
        user.setFirstName(outcome.getString("firstName"));
        user.setLastName(outcome.getString("lastName"));
        user.setImageUrl(outcome.getString("imageURL"));
        return user;
    }

    @Override
    public GetUserResponse findUser(GetUserRequest request) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey(partitionKey, request.getUserAlias());
        try {
            User user = getUser(spec);
            return new GetUserResponse(user);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new GetUserResponse("Error retrieving user.");
    }

    @Override
    public List<User> findMultiUsers(List<String> alias) {
        List<User> response = new ArrayList<>();
        for (int i = 0; i < alias.size(); i++) {
            GetItemSpec spec = new GetItemSpec().withPrimaryKey(partitionKey, alias.get(i));
            try {
                User user = getUser(spec);
                response.add(user);
            }
            catch (Exception e) {
                System.out.println(e.getMessage() + "HELLOLEOAWGADGDSF");
            }
        }
        return response;
    }

    @Override
    public User loginUser(LoginRequest request) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey(partitionKey, request.getUsername());
        try {
            User user = getUser(spec);
            Item outcome = table.getItem(spec);
            if (new Hashing().getHash(request.getPassword()).equals(outcome.getString("password"))) {
                return user;
            }
            else {
                throw new Exception("Incorrect Password");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + "HELLOLEOAWGADGDSF");
        }

        return null;
    }

    @Override
    public User registerUser(RegisterRequest request, String imageURL) {
        try {
            User newUser = new User(request.getFirstname(), request.getLastname(), request.getUsername(), imageURL);
            System.out.println("dao");
            //String hashedPassword = request.getPassword();
            String hashedPassword = new Hashing().getHash(request.getPassword());
            table.putItem(new Item().withString(partitionKey, request.getUsername())
                    .withString("firstName", request.getFirstname())
                    .withString("lastName", request.getLastname())
                    .withString("password", hashedPassword)
                    .withString("imageURL", imageURL));
            System.out.println(newUser.getAlias() + "dao");
            return newUser;
        }
        catch (Exception e) {
            System.out.println("Test this");
            //System.out.println(e.getMessage());
        }

        return null;
    }
}
