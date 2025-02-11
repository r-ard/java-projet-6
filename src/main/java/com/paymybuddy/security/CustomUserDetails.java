package com.paymybuddy.security;

import com.paymybuddy.entity.User;
import com.paymybuddy.repository.UserRepository;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class CustomUserDetails implements UserDetails {
    private UserRepository userRepository;

    private int userId;

    public CustomUserDetails(int userId, UserRepository userRepository) {
        this.userId = userId;
        this.userRepository = userRepository;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Nullable
    public User getUser() {
        Optional<User> user = userRepository.findById(this.userId);
        return user.orElse(null);
    }

    public int getId() { return userId; }

    @Override
    public String getPassword() {
        User user = this.getUser();
        return user == null ? "" : user.getPassword();
    }

    @Override
    public String getUsername() {
        User user = this.getUser();
        return user == null ? "" : user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
