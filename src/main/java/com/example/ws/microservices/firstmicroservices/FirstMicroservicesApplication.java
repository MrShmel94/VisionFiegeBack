package com.example.ws.microservices.firstmicroservices;

import com.example.ws.microservices.firstmicroservices.common.security.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(CorsProperties.class)
public class FirstMicroservicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstMicroservicesApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
