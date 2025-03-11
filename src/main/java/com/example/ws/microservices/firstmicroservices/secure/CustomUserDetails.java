package com.example.ws.microservices.firstmicroservices.secure;

import com.example.ws.microservices.firstmicroservices.dto.RoleDTO;
import com.example.ws.microservices.firstmicroservices.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {

    private final String userId;
    private final boolean isVerified;
    private final boolean isVerifiedEmail;
    private final boolean isCanHasAccount;
    private final String password;
    private final LocalDateTime validToAccount;
    private final LocalDateTime validFromAccount;

    @Builder.Default
    private final List<RoleDTO> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isEnabled() {
        return isVerified && isVerifiedEmail && isCanHasAccount && (validToAccount.isAfter(LocalDateTime.now()) && validFromAccount.isBefore(LocalDateTime.now()));
    }

}
