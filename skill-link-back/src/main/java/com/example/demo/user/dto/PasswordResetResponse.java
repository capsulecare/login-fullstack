package com.example.demo.user.dto;

public class PasswordResetResponse {

    private String mensaje;
    private boolean exito;
    private String correo;

    // Constructores
    public PasswordResetResponse() {}

    public PasswordResetResponse(String mensaje, boolean exito) {
        this.mensaje = mensaje;
        this.exito = exito;
    }

    public PasswordResetResponse(String mensaje, boolean exito, String correo) {
        this.mensaje = mensaje;
        this.exito = exito;
        this.correo = correo;
    }

    // Métodos de utilidad para crear respuestas comunes
    public static PasswordResetResponse exito(String mensaje, String correo) {
        return new PasswordResetResponse(mensaje, true, correo);
    }

    public static PasswordResetResponse error(String mensaje) {
        return new PasswordResetResponse(mensaje, false);
    }

    // Getters y Setters
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}