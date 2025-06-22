package skill_link.emprendedor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skill_link.emprendedor.dto.auth.ForgotPasswordRequest;
import skill_link.emprendedor.dto.auth.PasswordResetResponse;
import skill_link.emprendedor.dto.auth.ResetPasswordRequest;
import skill_link.emprendedor.entity.PasswordResetToken;
import skill_link.emprendedor.entity.User;
import skill_link.emprendedor.exception.EmailAlreadyExistsException;
import skill_link.emprendedor.repository.PasswordResetTokenRepository;
import skill_link.emprendedor.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    // private final EmailService emailService; // Descomenta cuando tengas servicio de email

    // Configuración del tiempo de expiración (en minutos)
    @Value("${app.password-reset.expiration-minutes:15}")
    private int expirationMinutes;

    /**
     * Genera y envía un token de recuperación de contraseña
     */
    @Transactional
    public PasswordResetResponse solicitarRecuperacion(ForgotPasswordRequest request) {
        try {
            log.info("Iniciando proceso de recuperación de contraseña para: {}", request.getCorreo());

            // 1. Verificar si el usuario existe
            User usuario = userRepository.findByCorreo(request.getCorreo())
                    .orElse(null);

            // Por seguridad, siempre devolvemos el mismo mensaje (no revelamos si el email existe)
            String mensajeSeguro = "Si el correo electrónico está registrado, recibirás un enlace de recuperación en breve.";

            if (usuario == null) {
                log.warn("Intento de recuperación para correo no registrado: {}", request.getCorreo());
                return PasswordResetResponse.exito(mensajeSeguro, request.getCorreo());
            }

            // 2. ✅ MEJORADO: Limpiar tokens expirados ANTES de verificar si existe uno válido
            LocalDateTime now = LocalDateTime.now();
            passwordResetTokenRepository.deleteExpiredTokens(now);
            log.info("Tokens expirados eliminados para verificación");

            // 3. ✅ MEJORADO: Verificar si ya existe un token válido DESPUÉS de limpiar expirados
            if (passwordResetTokenRepository.existsValidTokenForUsuario(usuario, now)) {
                log.info("Ya existe un token válido para el usuario: {}", request.getCorreo());
                return PasswordResetResponse.exito(mensajeSeguro, request.getCorreo());
            }

            // 4. ✅ MEJORADO: Eliminar TODOS los tokens anteriores del usuario (válidos y expirados)
            passwordResetTokenRepository.deleteByUsuario(usuario);
            log.info("Tokens anteriores eliminados para el usuario: {}", request.getCorreo());

            // 5. Crear nuevo token
            String tokenValue = UUID.randomUUID().toString();
            LocalDateTime expiry = now.plusMinutes(expirationMinutes);

            PasswordResetToken token = PasswordResetToken.builder()
                    .token(tokenValue)
                    .usuario(usuario)
                    .fechaCreacion(now)
                    .fechaExpiracion(expiry)
                    .usado(false)
                    .build();

            passwordResetTokenRepository.save(token);

            // 6. Enviar email (por ahora solo log)
            log.info("Token de recuperación generado para {}: {}", request.getCorreo(), tokenValue);
            log.info("Enlace de recuperación: http://localhost:5173/reset-password?token={}", tokenValue);
            log.info("Token expira el: {}", expiry);

            // TODO: Descomentar cuando tengas servicio de email
            // emailService.enviarEmailRecuperacion(usuario.getCorreo(), tokenValue);

            return PasswordResetResponse.exito(mensajeSeguro, request.getCorreo());

        } catch (Exception e) {
            log.error("Error al procesar solicitud de recuperación para: {}", request.getCorreo(), e);
            return PasswordResetResponse.error("Error interno del servidor. Inténtalo más tarde.");
        }
    }

    /**
     * Valida un token de recuperación
     */
    public PasswordResetResponse validarToken(String token) {
        try {
            log.info("Validando token de recuperación: {}", token);

            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                    .orElse(null);

            if (resetToken == null) {
                log.warn("Token no encontrado: {}", token);
                return PasswordResetResponse.error("Enlace inválido o expirado.");
            }

            // ✅ MEJORADO: Log detallado para debugging
            log.info("Token encontrado - Usado: {}, Expira: {}, Ahora: {}",
                    resetToken.isUsado(), resetToken.getFechaExpiracion(), LocalDateTime.now());

            if (!resetToken.isValido()) {
                if (resetToken.isUsado()) {
                    log.warn("Token ya fue usado: {}", token);
                    return PasswordResetResponse.error("Este enlace ya fue utilizado.");
                } else if (resetToken.isExpirado()) {
                    log.warn("Token expirado: {} - Expiró: {}", token, resetToken.getFechaExpiracion());
                    return PasswordResetResponse.error("Este enlace ha expirado. Solicita uno nuevo.");
                } else {
                    log.warn("Token inválido por razón desconocida: {}", token);
                    return PasswordResetResponse.error("Enlace inválido.");
                }
            }

            log.info("Token válido para usuario: {}", resetToken.getUsuario().getCorreo());
            return PasswordResetResponse.exito("Token válido", resetToken.getUsuario().getCorreo());

        } catch (Exception e) {
            log.error("Error al validar token: {}", token, e);
            return PasswordResetResponse.error("Error interno del servidor.");
        }
    }

    /**
     * Cambia la contraseña usando el token de recuperación
     */
    @Transactional
    public PasswordResetResponse cambiarContrasena(ResetPasswordRequest request) {
        try {
            log.info("Iniciando cambio de contraseña con token: {}", request.getToken());

            // 1. Buscar y validar token
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                    .orElse(null);

            if (resetToken == null) {
                log.warn("Token no encontrado para cambio de contraseña: {}", request.getToken());
                return PasswordResetResponse.error("Enlace inválido o expirado.");
            }

            // ✅ MEJORADO: Validación más específica
            if (!resetToken.isValido()) {
                if (resetToken.isUsado()) {
                    log.warn("Intento de usar token ya utilizado: {}", request.getToken());
                    return PasswordResetResponse.error("Este enlace ya fue utilizado.");
                } else if (resetToken.isExpirado()) {
                    log.warn("Intento de usar token expirado: {} - Expiró: {}",
                            request.getToken(), resetToken.getFechaExpiracion());
                    return PasswordResetResponse.error("Este enlace ha expirado. Solicita uno nuevo.");
                } else {
                    log.warn("Token inválido para cambio de contraseña: {}", request.getToken());
                    return PasswordResetResponse.error("Enlace inválido.");
                }
            }

            // 2. Obtener usuario y cambiar contraseña
            User usuario = resetToken.getUsuario();
            String nuevaContraEncriptada = passwordEncoder.encode(request.getNuevaContra());
            usuario.setContra(nuevaContraEncriptada);

            userRepository.save(usuario);

            // 3. Marcar token como usado
            resetToken.marcarComoUsado();
            passwordResetTokenRepository.save(resetToken);

            log.info("Contraseña cambiada exitosamente para usuario: {}", usuario.getCorreo());

            return PasswordResetResponse.exito(
                    "Contraseña cambiada exitosamente. Ya puedes iniciar sesión con tu nueva contraseña.",
                    usuario.getCorreo()
            );

        } catch (Exception e) {
            log.error("Error al cambiar contraseña con token: {}", request.getToken(), e);
            return PasswordResetResponse.error("Error interno del servidor. Inténtalo más tarde.");
        }
    }

    /**
     * Limpia tokens expirados (método de mantenimiento)
     */
    @Transactional
    public void limpiarTokensExpirados() {
        try {
            log.info("Iniciando limpieza de tokens expirados");
            int tokensEliminados = passwordResetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            log.info("Limpieza de tokens expirados completada - {} tokens eliminados", tokensEliminados);
        } catch (Exception e) {
            log.error("Error al limpiar tokens expirados", e);
        }
    }
}