package com.example.ws.microservices.firstmicroservices.secure;

import com.example.ws.microservices.firstmicroservices.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    @Getter
    private final String userId;
    @Getter
    private final String department;
    @Getter
    private final String username;
    @Getter
    private final String password;
    @Getter
    private final boolean isVerified;
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;

    @Getter
    private final String siteName;
    private List<GrantedAuthority> roles;
    @Getter
    private final String positionName;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }

}
