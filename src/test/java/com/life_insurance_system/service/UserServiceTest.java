package com.life_insurance_system.service;

import com.life_insurance_system.model.CustomerDetail;
import com.life_insurance_system.model.Role;
import com.life_insurance_system.model.User;
import com.life_insurance_system.repository.CustomerDetailRepository;
import com.life_insurance_system.repository.RoleRepository;
import com.life_insurance_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CustomerDetailRepository customerDetailRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginUser_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setActive(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.loginUser("testuser", "password");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testLoginUser_WrongPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setActive(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.loginUser("testuser", "wrongpassword");
        assertNull(result);
    }

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("newpassword");

        CustomerDetail customerDetail = new CustomerDetail();

        Role customerRole = new Role();
        customerRole.setRoleName("Customer");
        when(roleRepository.findByRoleName("Customer")).thenReturn(customerRole);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user, customerDetail);
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("Customer", result.getRole().getRoleName());
    }
}