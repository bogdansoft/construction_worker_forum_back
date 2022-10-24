package com.construction_worker_forum_back.model.security;

import org.springframework.security.core.GrantedAuthority;

public enum AccountStatus implements GrantedAuthority {
    CREATED, ACTIVE, DELETED;

    @Override
    public String getAuthority() {
        return name();
    }
}
