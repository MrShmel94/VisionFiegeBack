package com.example.ws.microservices.firstmicroservices;

import com.example.ws.microservices.firstmicroservices.secure.CorsProperties;
import com.example.ws.microservices.firstmicroservices.service.EmployeeMappingService;
import com.example.ws.microservices.firstmicroservices.utils.EmployeeInitializer;
import jakarta.validation.Validator;
import org.apache.commons.collections4.SplitMapUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashSet;
import java.util.Set;

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
