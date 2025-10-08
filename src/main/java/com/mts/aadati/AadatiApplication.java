package com.mts.aadati;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * © 2025  Mohamed Taha Elmeligy
 * This file is part of the eMTS Aadati Application.
 */

@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.mts.aadati.repository")
@EntityScan(basePackages = "com.mts.aadati.entities")
public class AadatiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AadatiApplication.class, args);
	}
}
