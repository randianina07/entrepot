package com.gestion.entrepot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.gestion")
public class EntrepotApplication {

	public static void main(String[] args) {
		SpringApplication.run(EntrepotApplication.class, args);
	}

}
