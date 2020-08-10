package com.robin.PandemyTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PandemyTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PandemyTrackerApplication.class, args);
	}

}