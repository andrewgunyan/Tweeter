package edu.byu.cs.tweeter.server.helpers;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class UpdateFeedRequest {
    List<String> aliases;
    Status status;

    public UpdateFeedRequest(List<String> aliases, Status status) {
        this.aliases = aliases;
        this.status = status;
    }

    public UpdateFeedRequest() {}

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
