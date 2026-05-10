package com.medreminder.medreminder_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.medreminder.medreminder_server.infrastructure.entity")
public class MedReminderServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(MedReminderServerApplication.class, args);
	}
}



//TODO 1:
// 1. Social authorization controller and updated record for it.
// 2. Subscription base setup. ✅
// 3. Payment system setup after the subscription base is done.
// 4. Schedule background work process for creating new Schedule events
//		for another seven days for medication profiles that are only still active.
//	But would it be better if we control this from front end and set up a background work
//	that will send the post request. OR we should do everything on Backend.
// 5. Mailing system set up.

// TODO 2:
// 1. Make a new field to track and accumulate each pills user take. ✅
// 2. Set up service and controller for creating medication pack directly and refill process. ✅
// 3. Update the event schedules so that when the medicationProfile is not active, it shouldn't
//		return it.✅

//CASE WITH PACK:
// When user adding a new pack when they didn't initially add it when they are creating
// the schedule profile, we need to:
//			Get the track amount of the current amountTaken. ✅
//			Subtract it from the total and then save it into the currentQuantity. ✅





//SUBSCRIPTION AND PAYMENT SET UP
// 1. A user can have multiple payments
// 2. Subscription should be created once for the period it will last.
// 		we create another one once it ended.
// 3. Each subscription has it own connection to a single payment entity.
// 4. Each subscription will reuse a plan. On initial, we create a free plan entity,
//	but when user pay for subscription, we create a new pro plan which we will use
// for the period of the subscription.


// User -> Plan (OneToOne)
// User -> Subscription (OneToOne)
// User -> Payments (OneToMany)
// Subscription -> SubscriptionPeriod (OneToMany)
// Subscription -> Plan(OneToOne);
// SubscriptionPeriod -> Payment(OneToOne)






















