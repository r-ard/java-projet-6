package com.paymybuddy.service;

import com.paymybuddy.entity.User;
import com.paymybuddy.exception.user.InvalidOldPasswordUpdateException;
import com.paymybuddy.exception.user.NewEqualsOldPasswordUpdateException;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    protected User generateTestUser() {
        return new User("TestUser", "test@email.com", "password", 0.D);
    }

    @Test
    public void testGetUserByEmail() {
        User testUser = this.generateTestUser();
        when(userRepository.findByEmail("test@email.com")).thenReturn(testUser);

        User result = userService.getByEmail("test@email.com");

        assertNotNull(result);
        assertEquals("TestUser", result.getUsername());
    }

    @Test
    public void testRegisterUser() {
        User testUser = this.generateTestUser();

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.registerUser(testUser);

        // Expects that Repository's save method has been called 1 time
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void testUpdateUserPasswordInvalidOldPassword() {
        User user = new User();
        user.setId(1);
        user.setPassword("encodedOldPassword");

        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

        assertThrows(InvalidOldPasswordUpdateException.class, () ->
                userService.updateUserPassword(user, oldPassword, newPassword));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUserPasswordNewEqualsOldPassword() {
        User user = new User();
        user.setId(1);
        user.setPassword("encodedPassword");

        String oldPassword = "password";
        String newPassword = "password";

        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        assertThrows(NewEqualsOldPasswordUpdateException.class, () ->
                userService.updateUserPassword(user, oldPassword, newPassword));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUserPasswordSuccess() throws Exception {
        User user = new User();
        user.setId(1);
        user.setPassword("encodedOldPassword");

        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        // Simulate oldPassword matches
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        // Simulate password encoding
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        userService.updateUserPassword(user, oldPassword, newPassword);

        // Check user's password is updated to encoded one
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository).save(user);
    }
}
