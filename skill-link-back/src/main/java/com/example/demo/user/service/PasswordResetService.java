package com.example.demo.user.service;

import com.example.demo.user.dto.ForgotPasswordRequest;
import com.example.demo.user.dto.PasswordResetResponse;
import com.example.demo.user.dto.ResetPasswordRequest;
import com.example.demo.user.model.PasswordResetToken;
import com.example.demo.user.model.User;
import com.example.demo.user.repository.PasswordResetTokenRepository;
import com.example.demo.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Configuración del tiempo de expiración (en minutos)
    @Value("${app.password-reset.expiration-minutes:15}")
    private int expirationMinutes;

    /**
     * Genera y envía un token de recuperación de contraseña
     */
    @Transactional
    public PasswordResetResponse solicitarRecuperacion(ForgotPasswordRequest request) {
        try {
            System.out.println("=== SOLICITUD DE RECUPERACIÓN ===");
            System.out.println("Email: " + request.getCorreo());

            // 1. Verificar si el usuario existe
            Optional<User> usuarioOpt = userRepository.findUserByEmail(request.getCorreo());

            // Por seguridad, siempre devolvemos el mismo mensaje (no revelamos si el email existe)
            String mensajeSeguro = "Si el correo electrónico está registrado, recibirás un enlace de recuperación en breve.";

            if (usuarioOpt.isEmpty()) {
                System.out.println("✗ Usuario NO encontrado: " + request.getCorreo());
                return PasswordResetResponse.exito(mensajeSeguro, request.getCorreo());
            }

            User usuario = usuarioOpt.get();
            System.out.println("✓ Usuario encontrado: " + request.getCorreo());

            // 2. Limpiar tokens expirados ANTES de verificar si existe uno válido
            LocalDateTime now = LocalDateTime.now();
            passwordResetTokenRepository.deleteExpiredTokens(now);
            System.out.println("✓ Tokens expirados eliminados");

            // 3. Verificar si ya existe un token válido DESPUÉS de limpiar expirados
            if (passwordResetTokenRepository.existsValidTokenForUsuario(usuario, now)) {
                System.out.println("⚠ Ya existe un token válido para el usuario: " + request.getCorreo());
                return PasswordResetResponse.exito(mensajeSeguro, request.getCorreo());
            }

            // 4. Eliminar TODOS los tokens anteriores del usuario (válidos y expirados)
            passwordResetTokenRepository.deleteByUsuario(usuario);
            System.out.println("✓ Tokens anteriores eliminados para el usuario: " + request.getCorreo());

            // 5. Crear nuevo token
            String tokenValue = UUID.randomUUID().toString();
            LocalDateTime expiry = now.plusMinutes(expirationMinutes);

            PasswordResetToken token = new PasswordResetToken(tokenValue, usuario, now, expiry);
            passwordResetTokenRepository.save(token);

            // 6. Logs para desarrollo (aquí iría el envío de email)
            System.out.println("✓ Token de recuperación generado para " + request.getCorreo() + ": " + tokenValue);
            System.out.println("📧 Enlace de recuperación: http://localhost:5173/reset-password?token=" + tokenValue);
            System.out.println("⏰ Token expira el: " + expiry);

            return PasswordResetResponse.exito(mensajeSeguro, request.getCorreo());

        } catch (Exception e) {
            System.err.println("Error al procesar solicitud de recuperación para: " + request.getCorreo());
            e.printStackTrace();
            return PasswordResetResponse.error("Estamos en mantenimiento. Intenta más tarde.");
        }
    }

    /**
     * Valida un token de recuperación
     */
    public PasswordResetResponse validarToken(String token) {
        try {
            System.out.println("=== VALIDACIÓN DE TOKEN ===");
            System.out.println("Token: " + token);

            Optional<PasswordResetToken> resetTokenOpt = passwordResetTokenRepository.findByToken(token);

            if (resetTokenOpt.isEmpty()) {
                System.out.println("✗ Token no encontrado: " + token);
                return PasswordResetResponse.error("Enlace inválido o expirado.");
            }

            PasswordResetToken resetToken = resetTokenOpt.get();

            // Log detallado para debugging
            System.out.println("Token encontrado - Usado: " + resetToken.isUsado() + 
                             ", Expira: " + resetToken.getFechaExpiracion() + 
                             ", Ahora: " + LocalDateTime.now());

            if (!resetToken.isValido()) {
                if (resetToken.isUsado()) {
                    System.out.println("✗ Token ya fue usado: " + token);
                    return PasswordResetResponse.error("Este enlace ya fue utilizado.");
                } else if (resetToken.isExpirado()) {
                    System.out.println("✗ Token expirado: " + token + " - Expiró: " + resetToken.getFechaExpiracion());
                    return PasswordResetResponse.error("Este enlace ha expirado. Solicita uno nuevo.");
                } else {
                    System.out.println("✗ Token inválido por razón desconocida: " + token);
                    return PasswordResetResponse.error("Enlace inválido.");
                }
            }

            System.out.println("✓ Token válido para usuario: " + resetToken.getUsuario().getEmail());
            return PasswordResetResponse.exito("Token válido", resetToken.getUsuario().getEmail());

        } catch (Exception e) {
            System.err.println("Error al validar token: " + token);
            e.printStackTrace();
            return PasswordResetResponse.error("Estamos en mantenimiento. Intenta más tarde.");
        }
    }

    /**
     * Cambia la contraseña usando el token de recuperación
     */
    @Transactional
    public PasswordResetResponse cambiarContrasena(ResetPasswordRequest request) {
        try {
            System.out.println("=== CAMBIO DE CONTRASEÑA ===");
            System.out.println("Token: " + request.getToken());

            // 1. Buscar y validar token
            Optional<PasswordResetToken> resetTokenOpt = passwordResetTokenRepository.findByToken(request.getToken());

            if (resetTokenOpt.isEmpty()) {
                System.out.println("✗ Token no encontrado para cambio de contraseña: " + request.getToken());
                return PasswordResetResponse.error("Enlace inválido o expirado.");
            }

            PasswordResetToken resetToken = resetTokenOpt.get();

            // Validación más específica
            if (!resetToken.isValido()) {
                if (resetToken.isUsado()) {
                    System.out.println("✗ Intento de usar token ya utilizado: " + request.getToken());
                    return PasswordResetResponse.error("Este enlace ya fue utilizado.");
                } else if (resetToken.isExpirado()) {
                    System.out.println("✗ Intento de usar token expirado: " + request.getToken() + 
                                     " - Expiró: " + resetToken.getFechaExpiracion());
                    return PasswordResetResponse.error("Este enlace ha expirado. Solicita uno nuevo.");
                } else {
                    System.out.println("✗ Token inválido para cambio de contraseña: " + request.getToken());
                    return PasswordResetResponse.error("Enlace inválido.");
                }
            }

            // 2. Obtener usuario y cambiar contraseña
            User usuario = resetToken.getUsuario();
            String nuevaContraEncriptada = passwordEncoder.encode(request.getNuevaContra());
            usuario.setPassword(nuevaContraEncriptada);

            userRepository.save(usuario);

            // 3. Marcar token como usado
            resetToken.marcarComoUsado();
            passwordResetTokenRepository.save(resetToken);

            System.out.println("✓ Contraseña cambiada exitosamente para usuario: " + usuario.getEmail());

            return PasswordResetResponse.exito(
                    "Contraseña cambiada exitosamente. Ya puedes iniciar sesión con tu nueva contraseña.",
                    usuario.getEmail()
            );

        } catch (Exception e) {
            System.err.println("Error al cambiar contraseña con token: " + request.getToken());
            e.printStackTrace();
            return PasswordResetResponse.error("Estamos en mantenimiento. Intenta más tarde.");
        }
    }

    /**
     * Limpia tokens expirados (método de mantenimiento)
     */
    @Transactional
    public void limpiarTokensExpirados() {
        try {
            System.out.println("=== LIMPIEZA DE TOKENS EXPIRADOS ===");
            int tokensEliminados = passwordResetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            System.out.println("✓ Limpieza completada - " + tokensEliminados + " tokens eliminados");
        } catch (Exception e) {
            System.err.println("Error al limpiar tokens expirados");
            e.printStackTrace();
        }
    }
}