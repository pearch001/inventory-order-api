package com.pearchInventory.ioa.controllers;


import com.pearchInventory.ioa.dtos.UserDTO;
import com.pearchInventory.ioa.enums.ResponseCode;
import com.pearchInventory.ioa.services.AuthService;
import com.pearchInventory.ioa.utils.GenericData;
import com.pearchInventory.ioa.utils.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody UserDTO userDTO) {
        String token = authService.register(userDTO);
        return ResponseEntity.status(201).body(new Response(ResponseCode.SUCCESS,new GenericData<>(token)) );
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@Valid @RequestBody UserDTO userDTO) {
        String token = authService.login(userDTO);
        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS,new GenericData<>(token)) );
    }
}