package com.medreminder.medreminder_server.domain.services.billing;

import com.medreminder.medreminder_server.domain.services.UseCase;

public interface SubscriptionService extends UseCase {

    void createSubscription();
}


// What do we need to create a new subscription for user?:
// To initiate subscription, user need to click on the payment route.