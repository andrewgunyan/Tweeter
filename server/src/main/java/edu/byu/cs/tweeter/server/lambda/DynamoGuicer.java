package edu.byu.cs.tweeter.server.lambda;
import com.google.inject.AbstractModule;

import edu.byu.cs.tweeter.server.dao.dynamo.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FolloweesDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FollowersDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.UserDAO;
import edu.byu.cs.tweeter.server.dao.iAuthTokenDao;
import edu.byu.cs.tweeter.server.dao.iFeedDao;
import edu.byu.cs.tweeter.server.dao.iFollowDao;
import edu.byu.cs.tweeter.server.dao.iFolloweesDao;
import edu.byu.cs.tweeter.server.dao.iFollowersDao;
import edu.byu.cs.tweeter.server.dao.iStoryDao;
import edu.byu.cs.tweeter.server.dao.iUserDao;

public class DynamoGuicer extends AbstractModule{
    @Override
    protected void configure() {
        bind(iAuthTokenDao.class).to(AuthTokenDAO.class);
        bind(iFeedDao.class).to(FeedDAO.class);
        bind(iFolloweesDao.class).to(FolloweesDAO.class);
        bind(iFollowersDao.class).to(FollowersDAO.class);
        bind(iStoryDao.class).to(StoryDAO.class);
        bind(iUserDao.class).to(UserDAO.class);
        bind(iFollowDao.class).to(FollowDAO.class);
    }
}
