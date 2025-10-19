package com.life_insurance_system.service;

import com.life_insurance_system.model.CustomerDetail;
import com.life_insurance_system.model.Role;
import com.life_insurance_system.model.User;
import com.life_insurance_system.repository.CustomerDetailRepository;
import com.life_insurance_system.repository.RoleRepository;
import com.life_insurance_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerDetailRepository customerDetailRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, CustomerDetailRepository customerDetailRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerDetailRepository = customerDetailRepository;
    }

    /**
     * Authenticates a user based on username and password (plain text).
     * @param username The user's username.
     * @param password The user's plain text password.
     * @return The User object if authentication is successful, otherwise null.
     */
    public User loginUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Plain text password check as requested
            if (user.getPassword().equals(password) && user.isActive()) {
                return user;
            }
        }
        return null;
    }

    /**
     * Registers a new customer in the system.
     * @param user The User object with registration details.
     * @param customerDetail The CustomerDetail object with additional info.
     * @return The saved User object.
     */
    @Transactional
    public User registerUser(User user, CustomerDetail customerDetail) {
        // Find the "Customer" role
        Role customerRole = roleRepository.findByRoleName("Customer");
        if (customerRole == null) {
            // This is a fallback, in a real app, roles should be pre-populated
            customerRole = new Role();
            customerRole.setRoleName("Customer");
            roleRepository.save(customerRole);
        }
        user.setRole(customerRole);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        customerDetail.setUser(savedUser);
        customerDetailRepository.save(customerDetail);

        return savedUser;
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User createStaffUser(User user, int roleId) {
        Role staffRole = roleRepository.findById(roleId).orElse(null);
        if (staffRole == null) {
            // Handle error, role not found
            return null;
        }
        user.setRole(staffRole);
        user.setActive(true);
        return userRepository.save(user);
    }

    public void deactivateUser(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setActive(false);
            userRepository.save(user);
        }
    }

    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    public void activateUser(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setActive(true);
            userRepository.save(user);
        }
    }

    public User updateUser(User user, String newPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }
        return userRepository.save(user);
    }

    public java.util.List<User> searchUsers(String keyword) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().contains(keyword) || user.getFirstName().contains(keyword) || user.getLastName().contains(keyword))
                .collect(java.util.stream.Collectors.toList());
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }
}