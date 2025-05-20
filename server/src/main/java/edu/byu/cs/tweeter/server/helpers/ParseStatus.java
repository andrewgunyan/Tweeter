package edu.byu.cs.tweeter.server.helpers;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dynamo.UserDAO;

public class ParseStatus {

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<Status> getStory(List<Item> items, User user) {
        List<Status> ans = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            String post = items.get(i).getString("post");
            String datetime = items.get(i).getString("statusDate");
            System.out.println(datetime);
            List<String> url = parseURLs(post);
            List<String> mentions = parseMentions(post);
            Status status = new Status(post, user, datetime, url, mentions);
            ans.add(status);
        }
        return ans;
    }

    public List<Status> getFeed(List<Item> items) {
        UserDAO userDAO = new UserDAO();
        List<Status> ans = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            String post = items.get(i).getString("post");
            String dateTime = items.get(i).getString("statusDate");
            GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", items.get(i).get("poster"));
            User user = userDAO.getUser(spec);
            List<String> url = parseURLs(post);
            List<String> mentions = parseMentions(post);
            Status status = new Status(post, user, dateTime, url, mentions);
            ans.add(status);
        }
        return ans;
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }
}