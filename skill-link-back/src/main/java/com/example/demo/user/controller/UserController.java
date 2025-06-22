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
            System.out.println("=== REGISTRO DE USUARIO ===");
            System.out.println("Datos recibidos: " + userRegisterRequest);
            
            // Validar que no sea admin
            if(userRegisterRequest.role().name().equals("Admin")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se puede registrar un usuario con el rol ADMIN."));
            }

            // Registrar usuario
            User user = userService.register(userRegisterRequest);
            System.out.println("Usuario creado con ID: " + user.getId());
            
            // Generar token automáticamente
            String jwtToken = tokenService.generateToken(user);
            System.out.println("Token generado exitosamente");
            
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
            System.err.println("Error de runtime: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("error", "Error interno del servidor."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequest userLoginRequest){
        try{
            System.out.println("=== LOGIN DE USUARIO ===");
            System.out.println("Email: " + userLoginRequest.email());
            
            // Autenticar usuario
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userLoginRequest.email(), userLoginRequest.password());
            
            Authentication authentication = authenticationManager.authenticate(authToken);
            User user = (User) authentication.getPrincipal();
            
            // Verificar si el usuario está activo
            if (!user.isActive()) {
                return ResponseEntity.status(403)
                    .body(Map.of("error", "Tu cuenta está desactivada."));
            }
            
            // Generar token
            String jwtToken = tokenService.generateToken(user);
            System.out.println("Login exitoso para usuario: " + user.getEmail());
            
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
            System.err.println("Credenciales incorrectas para: " + userLoginRequest.email());
            return ResponseEntity.status(401)
                .body(Map.of("error", "Email o contraseña incorrectos."));
        } catch (AuthenticationException e) {
            System.err.println("Error de autenticación: " + e.getMessage());
            return ResponseEntity.status(401)
                .body(Map.of("error", "Error de autenticación."));
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("error", "Error interno del servidor."));
        }
    }

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody Map<String, String> request){
        try{
            String email = request.get("correo");
            System.out.println("=== RECUPERACIÓN DE CONTRASEÑA ===");
            System.out.println("Email solicitado: " + email);
            
            if(email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "El email es requerido", "exito", false));
            }
            
            // Verificar si el usuario existe (solo para logs internos)
            boolean userExists = userService.existsByEmail(email);
            if(userExists) {
                System.out.println("✓ Usuario encontrado: " + email);
                // Aquí iría la lógica de envío de email
                System.out.println("📧 Se enviaría email de recuperación a: " + email);
            } else {
                System.out.println("✗ Usuario NO encontrado: " + email);
                // No revelamos si el usuario existe o no
            }
            
            // SIEMPRE devolver el mismo mensaje exitoso para no revelar si el email existe
            return ResponseEntity.ok(Map.of(
                "mensaje", "Si el email está registrado, recibirás un enlace de recuperación en tu bandeja de entrada.",
                "exito", true
            ));
            
        } catch (Exception e) {
            System.err.println("Error en recuperación de contraseña: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("mensaje", "Estamos en mantenimiento. Intenta más tarde.", "exito", false));
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token){
        try{
            System.out.println("=== VALIDACIÓN DE TOKEN ===");
            System.out.println("Token recibido: " + token);
            
            // Por ahora, simulamos que todos los tokens son válidos
            // En producción aquí validarías el token contra la base de datos
            if(token != null && !token.trim().isEmpty()) {
                System.out.println("✓ Token válido (simulado)");
                return ResponseEntity.ok(Map.of(
                    "mensaje", "Token válido",
                    "exito", true,
                    "correo", "usuario@ejemplo.com" // Email simulado
                ));
            } else {
                System.out.println("✗ Token inválido");
                return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Token inválido o expirado", "exito", false));
            }
            
        } catch (Exception e) {
            System.err.println("Error validando token: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("mensaje", "Estamos en mantenimiento. Intenta más tarde.", "exito", false));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request){
        try{
            String token = request.get("token");
            String newPassword = request.get("nuevaContra");
            
            System.out.println("=== CAMBIO DE CONTRASEÑA ===");
            System.out.println("Token: " + token);
            System.out.println("Nueva contraseña recibida: " + (newPassword != null ? "✓" : "✗"));
            
            if(token == null || newPassword == null || token.trim().isEmpty() || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Token y nueva contraseña son requeridos", "exito", false));
            }
            
            // Por ahora simulamos que el cambio es exitoso
            // En producción aquí cambiarías la contraseña en la base de datos
            System.out.println("✓ Contraseña cambiada exitosamente (simulado)");
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Contraseña cambiada exitosamente. Ya puedes iniciar sesión.",
                "exito", true
            ));
            
        } catch (Exception e) {
            System.err.println("Error cambiando contraseña: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("mensaje", "Estamos en mantenimiento. Intenta más tarde.", "exito", false));
        }
    }
}