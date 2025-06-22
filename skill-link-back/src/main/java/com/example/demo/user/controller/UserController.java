package com.example.demo.user.controller;

import com.example.demo.user.service.UserService;
import com.example.demo.user.dto.*;
import com.example.demo.infra.security.TokenService;
import com.example.demo.user.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
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
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se puede registrar un usuario con el rol ADMIN directamente."));
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
                user.getInterests()
            );
            
            UserRegisterResponse response = new UserRegisterResponse(jwtToken, userResponse);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Error interno del servidor. Inténtalo de nuevo."));
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
            
            // Verificar si el usuario está activo
            if (!user.isActive()) {
                return ResponseEntity.status(403)
                    .body(Map.of("error", "Tu cuenta está desactivada. Contacta al administrador."));
            }
            
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
                user.getInterests()
            );
            
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Email o contraseña incorrectos. Verifica tus credenciales."));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Error de autenticación. Verifica tus credenciales."));
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("error", "Error interno del servidor. Inténtalo de nuevo."));
        }
    }
}