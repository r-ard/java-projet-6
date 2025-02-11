package com.paymybuddy.service;

import com.paymybuddy.entity.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if(user == null) {
            user = userRepository.findByEmail(username);
        }

        if(user == null) {
            throw new UsernameNotFoundException("User not found with following username/email : '" + username + "'");
        }

        return new CustomUserDetails(user.getId(), userRepository);
    }
}
