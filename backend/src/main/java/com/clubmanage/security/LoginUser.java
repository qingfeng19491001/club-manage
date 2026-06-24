package com.clubmanage.security;

import com.clubmanage.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class LoginUser implements UserDetails {

    private final User user;

    public LoginUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        int role = user.getRole() != null ? user.getRole() : 0;
        String roleStr;
        switch (role) {
            case 2:
                roleStr = "ROLE_ADMIN";
                break;
            case 1:
                roleStr = "ROLE_LEADER";
                break;
            default:
                roleStr = "ROLE_MEMBER";
                break;
        }
        return List.of(new SimpleGrantedAuthority(roleStr));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != null && user.getStatus() == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isAccountNonLocked();
    }
}