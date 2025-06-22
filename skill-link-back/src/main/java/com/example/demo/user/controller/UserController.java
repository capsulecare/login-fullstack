package com.example.demo.user.controller;

import com.example.demo.user.service.UserService;
import com.example.demo.user.dto.*;
import com.example.demo.infra.security.TokenService;
import com.example.demo.user.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        try{
            // Validar que no sea admin
            if(userRegisterRequest.role().name().equals("Admin")) {
                return ResponseEntity.badRequest().body("No se puede registrar un usuario con el rol ADMIN directamente. Use el endpoint de administración.");
            }

            // Registrar usuario
            User user = userService.register(userRegisterRequest);
            
            // Generar token automáticamente
            String jwtToken = tokenService.generateToken(user);
            
            // Crear respuesta
            UserRegisterResponse.UserResponse userResponse = new UserRegisterResponse.UserResponse(
                user.getId(), 
                user.getName(), 
                user.getSecondName(), 
                user.getEmail(), 
                user.getRole(),
                user.getInterests()  // <-- Ahora funciona con getInterests()
            );
            
            UserRegisterResponse response = new UserRegisterResponse(jwtToken, userResponse);
            
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar el usuario: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequest userLoginRequest){
        try{
            // Autenticar usuario
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userLoginRequest.email(), userLoginRequest.password());
            
            Authentication authentication = authenticationManager.authenticate(authToken);
            User user = (User) authentication.getPrincipal();
            
            // Generar token
            String jwtToken = tokenService.generateToken(user);
            
            // Crear respuesta
            UserLoginResponse response = new UserLoginResponse(
                jwtToken,
                user.getId(),
                user.getName(),
                user.getSecondName(),
                user.getEmail(),
                user.getRole(),
                user.getInterests()  // <-- Ahora funciona con getInterests()
            );
            
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al iniciar sesión: " + e.getMessage());
        }
    }
}