package com.entrepot.gestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.entrepot.gestion.repository")
public class GestionEntrepotApplication {
    public static void main(String[] args) {
        SpringApplication.run(GestionEntrepotApplication.class, args);
    }
}
