package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.entity.vision.RefreshToken;
import com.example.ws.microservices.firstmicroservices.repository.RefreshTokenRepository;
import com.example.ws.microservices.firstmicroservices.secure.SecurityConstants;
import com.example.ws.microservices.firstmicroservices.service.RefreshTokenService;
import com.example.ws.microservices.firstmicroservices.utils.Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Utils utils;

    @Override
    @Transactional
    public String createRefreshToken(String userId, HttpServletRequest request) {

        String refreshToken = utils.generateRefreshToken(userId, request);
        RefreshToken newToken = new RefreshToken();
        newToken.setToken(refreshToken);
        newToken.setUserId(userId);
        newToken.setIpAddress(utils.getClientIp(request));
        newToken.setUserAgent(request.getHeader("User-Agent"));
        newToken.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.REFRESH_TOKEN_EXPIRATION));
        refreshTokenRepository.save(newToken);

        return refreshToken;
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = utils.parseToken(token);
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByTokenAndUserId(token, claims.getSubject());
            return refreshToken.isPresent() && !refreshToken.get().getRevoked() && !isExpired(refreshToken.get());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        refreshToken.ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Override
    public String getSubjectFromToken(String token) {
        return utils.parseToken(token).getSubject();
    }


    @Override
    @Transactional
    public void deleteTokensForUser(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private boolean isExpired(RefreshToken refreshToken) {
        return refreshToken.getExpiration().before(new Date());
    }
}
