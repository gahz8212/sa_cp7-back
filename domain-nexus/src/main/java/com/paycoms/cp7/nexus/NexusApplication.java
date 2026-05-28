package com.paycoms.cp7.nexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.paycoms.cp7")
public class NexusApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexusApplication.class, args);
	}

}
