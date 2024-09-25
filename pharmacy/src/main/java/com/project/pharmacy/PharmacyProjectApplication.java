package com.project.pharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PharmacyProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmacyProjectApplication.class, args);
	}

}
