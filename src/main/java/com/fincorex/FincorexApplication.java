package com.fincorex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FincorexApplication {

	public static void main(String[] args) {
		SpringApplication.run(FincorexApplication.class, args);
	}

}
