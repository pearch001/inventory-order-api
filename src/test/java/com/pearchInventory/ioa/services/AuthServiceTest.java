package com.pearchInventory.ioa.services;

import com.pearchInventory.ioa.config.JwtUtil;
import com.pearchInventory.ioa.dtos.UserDTO;
import com.pearchInventory.ioa.exceptions.BadCredentialsException;
import com.pearchInventory.ioa.exceptions.EmailExistsException;
import com.pearchInventory.ioa.exceptions.UserNotFoundException;
import com.pearchInventory.ioa.model.User;
import com.pearchInventory.ioa.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_Success() {
        UserDTO userDTO = new UserDTO("user1", "password");
        User user = new User();
        user.setUsername(userDTO.username());
        user.setPassword("hashedPassword");

        when(passwordEncoder.encode(userDTO.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(userDTO.username())).thenReturn("jwtToken");

        String token = authService.register(userDTO);

        assertNotNull(token);
        assertEquals("jwtToken", token);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_EmailExistsException() {
        UserDTO userDTO = new UserDTO("user1", "password");

        when(passwordEncoder.encode(userDTO.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(EmailExistsException.class, () -> authService.register(userDTO));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_BadCredentialsException() {
        UserDTO userDTO = new UserDTO("user1", "password");

        when(passwordEncoder.encode(userDTO.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class);

        assertThrows(BadCredentialsException.class, () -> authService.register(userDTO));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_Success() {
        UserDTO userDTO = new UserDTO("user1", "password");
        User user = new User();
        user.setUsername(userDTO.username());
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDTO.password(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(userDTO.username())).thenReturn("jwtToken");

        String token = authService.login(userDTO);

        assertNotNull(token);
        assertEquals("jwtToken", token);
        verify(userRepository).findByUsername(userDTO.username());
    }

    @Test
    void login_UserNotFoundException() {
        UserDTO userDTO = new UserDTO("user1", "password");

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(userDTO));
        verify(userRepository).findByUsername(userDTO.username());
    }

    @Test
    void login_BadCredentialsException() {
        UserDTO userDTO = new UserDTO("user1", "password");
        User user = new User();
        user.setUsername(userDTO.username());
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDTO.password(), user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(userDTO));
        verify(userRepository).findByUsername(userDTO.username());
    }

    @Test
    void loadUserByUsername_Success() {
        User user = new User();
        user.setUsername("user1");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        User result = authService.loadUserByUsername("user1");

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        verify(userRepository).findByUsername("user1");
    }

    @Test
    void loadUserByUsername_UserNotFoundException() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.loadUserByUsername("user1"));
        verify(userRepository).findByUsername("user1");
    }
}