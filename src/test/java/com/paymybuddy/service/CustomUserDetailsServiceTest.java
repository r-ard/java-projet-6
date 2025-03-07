package com.paymybuddy.service;

import com.paymybuddy.entity.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CustomUserDetailsServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    protected User generateTestUser() {
        User user = new User("TestUser", "test@email.com", "password", 0.D);
        user.setId(1);
        return user;
    }

    @Test
    public void testLoadUserByEmailSuccess() throws Exception {
        User testUser = this.generateTestUser();
        when(userRepository.findByEmail("test@email.com")).thenReturn(testUser);

        UserDetails result = customUserDetailsService.loadUserByUsername("test@email.com");

        assertNotNull(result);
        assertInstanceOf(CustomUserDetails.class, result);

        assertEquals(((CustomUserDetails)result).getId(), testUser.getId());
    }

    @Test
    public void testLoadUserByUsernameSuccess() throws Exception {
        User testUser = this.generateTestUser();
        when(userRepository.findByUsername("TestUser")).thenReturn(testUser);

        UserDetails result = customUserDetailsService.loadUserByUsername("TestUser");

        assertNotNull(result);
        assertInstanceOf(CustomUserDetails.class, result);

        assertEquals(((CustomUserDetails)result).getId(), testUser.getId());
    }

    @Test
    public void testLoadUserFails() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("TestUser");
        });
    }
}
