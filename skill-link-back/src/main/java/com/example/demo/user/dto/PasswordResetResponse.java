package skill_link.emprendedor.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetResponse {

    private String mensaje;
    private boolean exito;
    private String correo; // Para confirmar a qué correo se envió

    // Métodos de utilidad para crear respuestas comunes
    public static PasswordResetResponse exito(String mensaje, String correo) {
        return PasswordResetResponse.builder()
                .mensaje(mensaje)
                .exito(true)
                .correo(correo)
                .build();
    }

    public static PasswordResetResponse error(String mensaje) {
        return PasswordResetResponse.builder()
                .mensaje(mensaje)
                .exito(false)
                .build();
    }
}