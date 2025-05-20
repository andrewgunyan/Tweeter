package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public interface iAuthTokenDao {

    public boolean isValidToken(AuthToken authToken);
    public LogoutResponse logout(LogoutRequest request);
    public AuthToken createToken(String handle);
}
