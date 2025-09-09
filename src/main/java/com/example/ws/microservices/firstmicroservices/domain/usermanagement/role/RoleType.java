package com.example.ws.microservices.firstmicroservices.domain.usermanagement.role;

import lombok.Getter;

@Getter
public enum RoleType {

    USER(1),
    MENTOR(2),
    TEAM_LEADER(3),
    MANAGER(4),
    OPERATIONS_MANAGER(5),
    ADMIN(10);

    private final int priority;

    RoleType(int priority) {
        this.priority = priority;
    }

    public boolean isHigherOrEqual(RoleType roleType) {
        return this.priority >= roleType.priority;
    }

}
