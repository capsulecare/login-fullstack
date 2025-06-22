package skill_link.emprendedor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario; // Mantengo la convención en español como tu User

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    private boolean usado = false;

    // Método de utilidad para verificar si el token ha expirado
    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(this.fechaExpiracion);
    }

    // Método de utilidad para verificar si el token es válido
    public boolean isValido() {
        return !usado && !isExpirado();
    }

    // Método para marcar el token como usado
    public void marcarComoUsado() {
        this.usado = true;
    }
}