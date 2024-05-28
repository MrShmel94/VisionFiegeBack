package com.example.ws.microservices.firstmicroservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class FirstMicroservicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstMicroservicesApplication.class, args);
    }

}
