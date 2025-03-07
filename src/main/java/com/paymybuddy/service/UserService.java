package com.paymybuddy.service;

import com.paymybuddy.dto.UserRegistrationDTO;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.user.EmailAlreadyTakenException;
import com.paymybuddy.exception.user.InvalidOldPasswordUpdateException;
import com.paymybuddy.exception.user.NewEqualsOldPasswordUpdateException;
import com.paymybuddy.exception.user.UsernameAlreadyTakenException;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class UserService {
    @Value("${app.defaultbalance}")
    private String defaultBalance;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(UserRegistrationDTO userDto) throws EmailAlreadyTakenException, UsernameAlreadyTakenException {
        if(this.isEmailTaken(userDto.getEmail())) {
            throw new EmailAlreadyTakenException(userDto.getEmail());
        }

        if(this.isUsernameTaken(userDto.getUsername())) {
            throw new UsernameAlreadyTakenException(userDto.getUsername());
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setBalance(this.getDefaultBalance());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

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
        return this.getByEmail(email) != null;
    }

    public boolean isUsernameTaken(String username) {
        return this.getByUsername(username) != null;
    }

    protected double getDefaultBalance() {
        try {
            return Double.parseDouble(this.defaultBalance);
        }
        catch(Exception e) {
            return 50.0D;
        }
    }
}
