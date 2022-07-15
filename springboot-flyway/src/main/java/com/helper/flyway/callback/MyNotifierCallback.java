package com.helper.flyway.callback;

import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;

/**
 * @author jaydon
 */
public class MyNotifierCallback implements Callback {
    
    // Ensures that this callback handles both events
    @Override
    public boolean supports(Event event, Context context) {
        //return event == Event.AFTER_EACH_MIGRATE;
       return event.equals(Event.AFTER_MIGRATE) || event.equals(Event.AFTER_MIGRATE_ERROR);
    }
    
    // Not relevant if we don't interact with the database
    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }
    
    // Send a notification when either event happens.
    @Override
    public void handle(Event event, Context context) {
        String notification = event.equals(Event.AFTER_MIGRATE) ? "Success" : "Failed";
        System.out.println("jaydon Callback notification = " + notification);
        // ... Notification logic ...
     //   notificationService.send(notification);
    }

    String getCallbackName() {
        return "MyNotifier";
    }
}