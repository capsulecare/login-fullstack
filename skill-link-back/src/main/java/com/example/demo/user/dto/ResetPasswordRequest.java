package com.example.demo.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String nuevaContra;

    // Constructores
    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String token, String nuevaContra) {
        this.token = token;
        this.nuevaContra = nuevaContra;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNuevaContra() {
        return nuevaContra;
    }

    public void setNuevaContra(String nuevaContra) {
        this.nuevaContra = nuevaContra;
    }
}