package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerResponse extends Response {


    private Boolean bool;
    public IsFollowerResponse(Boolean bool) {
        super(true, null);
        this.bool = bool;
    }

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public Boolean getBool() {
        return bool;
    }
}
