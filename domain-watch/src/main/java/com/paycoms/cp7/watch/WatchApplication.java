package com.paycoms.cp7.watch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.paycoms.cp7")
public class WatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatchApplication.class, args);
	}

}
