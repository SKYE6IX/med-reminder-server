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

//TODO:
// 1. Mailing system set up






