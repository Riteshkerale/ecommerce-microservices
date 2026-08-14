package com.ritesh.Eureka_ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@SpringBootApplication

@EnableEurekaServer
public class EurekaEcomApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaEcomApplication.class, args);
	}

}
