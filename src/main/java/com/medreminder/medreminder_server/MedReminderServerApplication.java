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
// 1. Social authorization controller and updated record for it.✅
// 2. Subscription base setup. ✅
// 3. Payment system setup after the subscription base is done. ✅
// 4. Schedule background work:
//	Items:
//	Medication schedule events
//	Renew Subscriptions fee for due
//	Downgrade plan for cancelled subscription.
// 5. Mailing system set up





















