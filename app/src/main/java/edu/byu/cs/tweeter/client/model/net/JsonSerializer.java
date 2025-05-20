package edu.byu.cs.tweeter.client.model.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class JsonSerializer {

    public static String serialize(Object requestInfo) {
        return (new Gson()).toJson(requestInfo);
    }

    public static <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }

    public static <T> List<T> deserializeList(String value, TypeToken<List<T>> typeToken) {
        return (new Gson()).fromJson(value, typeToken.getType());
    }
}
