package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.byu.cs.tweeter.server.service.StatusService;

public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        Injector injector = Guice.createInjector(new DynamoGuicer());
        StatusService service = injector.getInstance(StatusService.class);
        service.UpdateFeedMessages(input, context);
        return null;
    }
}
