package com.pearchInventory.ioa.services;

import com.pearchInventory.ioa.config.JwtUtil;
import com.pearchInventory.ioa.dtos.UserDTO;
import com.pearchInventory.ioa.exceptions.BadCredentialsException;
import com.pearchInventory.ioa.exceptions.EmailExistsException;
import com.pearchInventory.ioa.exceptions.UserNotFoundException;
import com.pearchInventory.ioa.model.User;
import com.pearchInventory.ioa.repositories.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // Marks this class as a Spring service component
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Constructor-based dependency injection for required components
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user by saving their details in the database.
     * The password is encoded before saving for security.
     * A JWT token is generated and returned upon successful registration.
     *
     * @param userDTO Data Transfer Object containing user details
     * @return A JWT token for the registered user
     */
    public String register(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.username()); // Set the username from the DTO
        user.setPassword(passwordEncoder.encode(userDTO.password())); // Encode and set the password
        try {
            userRepository.save(user); // Save the user to the database
        } catch (DataIntegrityViolationException e) {
            // Handle database constraint violations (e.g., unique constraint)
            throw new EmailExistsException("A user with this username already exists");
        } catch (Exception e) {
            // Handle other unexpected exceptions
            throw new BadCredentialsException("An error occurred while saving the user");
        }
        return jwtUtil.generateToken(user.getUsername()); // Generate and return a JWT token
    }

    /**
     * Authenticates a user by verifying their credentials.
     * A JWT token is generated and returned upon successful authentication.
     *
     * @param userDTO Data Transfer Object containing login credentials
     * @return A JWT token for the authenticated user
     */
    public String login(UserDTO userDTO) {
        // Fetch the user by username, throw an exception if not found
        User user = userRepository.findByUsername(userDTO.username())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Verify the provided password matches the stored password
        if (passwordEncoder.matches(userDTO.password(), user.getPassword())) {
            return jwtUtil.generateToken(user.getUsername()); // Generate and return a JWT token
        }

        // Throw an exception if credentials are invalid
        throw new BadCredentialsException("Invalid credentials");
    }

    /**
     * Loads a user by their username for authentication purposes.
     * This method is required by the UserDetailsService interface.
     *
     * @param username The username of the user to load
     * @return The User entity with the specified username
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found")); // Handle missing user
    }
}