package com.example.ws.microservices.firstmicroservices.entity.enums;

import lombok.Getter;

@Getter
public enum Role {

    USER(1),
    MENTOR(2),
    TEAM_LEADER(3),
    MANAGER(4),
    OPERATIONS_MANAGER(5),
    ADMIN(10);

    private final int priority;

    Role(int priority) {
        this.priority = priority;
    }

    public boolean isHigherOrEqual(Role role) {
        return this.priority >= role.priority;
    }

}
