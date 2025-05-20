package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusResponse extends Response {
    private Status status;

    public PostStatusResponse() {
        super(true, null);
    }

    public PostStatusResponse(String message) {
        super(false, message);
    }

    public PostStatusResponse(Status status) {
        super(true);
        this.status = status;
    }
}
