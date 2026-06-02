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
// 4. Schedule background work process for creating new Schedule events
//		for another seven days for medication profiles that are only still active.
//	But would it be better if we control this from front end and set up a background work
//	that will send the post request. OR we should do everything on Backend.
// This will be called from frontend, to identify the user, and to make sure user is
// still an active user, and we are not creating data for user that stopped using
// the app.
// 5. Mailing system set up.























