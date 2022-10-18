package com.construction_worker_forum_back.model.security;

import com.construction_worker_forum_back.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter @Setter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails, UserDetailsMixin {

    private User user;

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<GrantedAuthority>();

        authorities.add(user.getUserRoles());

        if (user.getUserRoles() == Role.USER) {
            authorities.add(user.getAccountStatus());
        } else {
            authorities.add(AccountStatus.ACTIVE);
        }

        return authorities;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountStatus() != AccountStatus.DELETED;
    }
}
