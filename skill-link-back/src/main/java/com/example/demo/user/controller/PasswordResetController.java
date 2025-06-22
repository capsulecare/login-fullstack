package skill_link.emprendedor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skill_link.emprendedor.dto.auth.ForgotPasswordRequest;
import skill_link.emprendedor.dto.auth.PasswordResetResponse;
import skill_link.emprendedor.dto.auth.ResetPasswordRequest;
import skill_link.emprendedor.service.PasswordResetService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Ajusta según tu frontend
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    /**
     * Endpoint para solicitar recuperación de contraseña
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponse> solicitarRecuperacion(
            @Valid @RequestBody ForgotPasswordRequest request) {

        log.info("Solicitud de recuperación de contraseña recibida para: {}", request.getCorreo());

        PasswordResetResponse response = passwordResetService.solicitarRecuperacion(request);

        // Siempre devolvemos 200 OK por seguridad (no revelamos si el email existe)
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para validar un token de recuperación
     * GET /api/auth/validate-reset-token?token=xxx
     */
    @GetMapping("/validate-reset-token")
    public ResponseEntity<PasswordResetResponse> validarToken(
            @RequestParam("token") String token) {

        log.info("Validación de token solicitada");

        PasswordResetResponse response = passwordResetService.validarToken(token);

        if (response.isExito()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Endpoint para cambiar contraseña con token
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponse> cambiarContrasena(
            @Valid @RequestBody ResetPasswordRequest request) {

        log.info("Solicitud de cambio de contraseña recibida");

        PasswordResetResponse response = passwordResetService.cambiarContrasena(request);

        if (response.isExito()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Endpoint de mantenimiento para limpiar tokens expirados
     * DELETE /api/auth/cleanup-expired-tokens
     * (Solo para administradores o tareas programadas)
     */
    @DeleteMapping("/cleanup-expired-tokens")
    public ResponseEntity<String> limpiarTokensExpirados() {

        log.info("Solicitud de limpieza de tokens expirados");

        try {
            passwordResetService.limpiarTokensExpirados();
            return ResponseEntity.ok("Tokens expirados eliminados exitosamente");
        } catch (Exception e) {
            log.error("Error al limpiar tokens expirados", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al limpiar tokens expirados");
        }
    }
}