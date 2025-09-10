package com.example.ws.microservices.firstmicroservices.common.security;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {

    private final String userId;
    @Getter
    private final String expertis;
    private final String siteName;
    private final boolean isVerified;
    private final boolean isVerifiedEmail;
    private final boolean isCanHasAccount;
    private final String password;
    private final LocalDate validToAccount;
    private final LocalDate validFromAccount;

    @Builder.Default
    private final List<UserRoleDTO> roles = new ArrayList<>();

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
        return isVerified && isVerifiedEmail && isCanHasAccount && (validToAccount.isAfter(LocalDate.now()) && validFromAccount.isBefore(LocalDate.now()));
    }

}
