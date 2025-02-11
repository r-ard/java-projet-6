package com.paymybuddy.service;

import com.paymybuddy.entity.User;
import com.paymybuddy.exception.user.InvalidOldPasswordUpdateException;
import com.paymybuddy.exception.user.NewEqualsOldPasswordUpdateException;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUserPassword(User user, String oldPassword, String newPassword) throws InvalidOldPasswordUpdateException, NewEqualsOldPasswordUpdateException {
        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidOldPasswordUpdateException();
        }

        if(oldPassword.equals(newPassword)) {
            throw new NewEqualsOldPasswordUpdateException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User getByEmail(String email) { return userRepository.findByEmail(email); }

    public User getByUsername(String username) { return userRepository.findByUsername(username); }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username) != null;
    }
}
