package com.example.ws.microservices.firstmicroservices.repository;

import com.example.ws.microservices.firstmicroservices.entity.vision.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByTokenAndUserId(String token, String userId);
    void deleteByUserId(String userId);
}
