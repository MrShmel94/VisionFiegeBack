package com.example.ws.microservices.firstmicroservices.entity;

import com.example.ws.microservices.firstmicroservices.entity.enums.Role;
import com.example.ws.microservices.firstmicroservices.entity.template.Department;
import com.example.ws.microservices.firstmicroservices.entity.template.Position;
import com.example.ws.microservices.firstmicroservices.entity.template.Site;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private List<Role> roles;
    private String expertis;
    private String departmentName;
    private String positionName;
    private String siteName;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }
}
