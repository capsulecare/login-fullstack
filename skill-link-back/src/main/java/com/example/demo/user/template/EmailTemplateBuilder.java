package com.example.demo.user.template;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateBuilder {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public String construirPlantillaRecuperacion(String nombreUsuario, String enlaceRecuperacion) {
        return "<!DOCTYPE html>" +
            "<html lang=\"es\">" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Recuperación de Contraseña - SkillLink</title>" +
                "<style>" +
                    "* {" +
                        "margin: 0;" +
                        "padding: 0;" +
                        "box-sizing: border-box;" +
                    "}" +
                    
                    "body {" +
                        "font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;" +
                        "line-height: 1.6;" +
                        "color: #333;" +
                        "background-color: #f3f4f6;" +
                        "padding: 20px;" +
                    "}" +
                    
                    ".container {" +
                        "max-width: 600px;" +
                        "margin: 0 auto;" +
                        "background: white;" +
                        "border-radius: 12px;" +
                        "box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);" +
                        "overflow: hidden;" +
                        "border: 1px solid #e5e7eb;" +
                    "}" +
                    
                    ".header {" +
                        "background-color: #7c3aed;" +
                        "padding: 30px;" +
                        "text-align: center;" +
                    "}" +
                    
                    ".logo {" +
                        "display: flex;" +
                        "flex-direction: column;" +
                        "align-items: center;" +
                        "gap: 8px;" +
                        "margin-bottom: 15px;" +
                    "}" +
                    
                    ".logo-icon {" +
                        "font-size: 32px;" +
                        "line-height: 1;" +
                    "}" +
                    
                    ".logo-text {" +
                        "color: white;" +
                        "font-size: 28px;" +
                        "font-weight: 700;" +
                        "margin: 0;" +
                    "}" +
                    
                    ".header-subtitle {" +
                        "color: rgba(255, 255, 255, 0.9);" +
                        "font-size: 14px;" +
                        "font-weight: 500;" +
                        "margin: 0;" +
                    "}" +
                    
                    ".content {" +
                        "padding: 40px 30px;" +
                        "background: white;" +
                    "}" +
                    
                    ".greeting {" +
                        "font-size: 22px;" +
                        "font-weight: 600;" +
                        "color: #1f2937;" +
                        "margin-bottom: 20px;" +
                        "text-align: center;" +
                    "}" +
                    
                    ".message {" +
                        "font-size: 16px;" +
                        "color: #4b5563;" +
                        "margin-bottom: 30px;" +
                        "text-align: justify;" +
                        "line-height: 1.7;" +
                    "}" +
                    
                    ".cta-container {" +
                        "text-align: center;" +
                        "margin: 30px 0;" +
                    "}" +
                    
                    ".cta-button {" +
                        "display: inline-block;" +
                        "background-color: #7c3aed;" +
                        "color: white;" +
                        "text-decoration: none;" +
                        "padding: 14px 28px;" +
                        "border-radius: 8px;" +
                        "font-weight: 700;" +
                        "font-size: 16px;" +
                        "transition: background-color 0.3s ease;" +
                    "}" +
                    
                    ".cta-button:hover {" +
                        "background-color: #6d28d9;" +
                    "}" +
                    
                    ".security-notice {" +
                        "background-color: #fef3c7;" +
                        "border: 1px solid #f59e0b;" +
                        "border-radius: 8px;" +
                        "padding: 20px;" +
                        "margin: 25px 0;" +
                    "}" +
                    
                    ".security-title {" +
                        "font-weight: 600;" +
                        "color: #92400e;" +
                        "margin-bottom: 10px;" +
                        "font-size: 16px;" +
                    "}" +
                    
                    ".security-text {" +
                        "color: #b45309;" +
                        "font-size: 14px;" +
                        "line-height: 1.6;" +
                        "text-align: justify;" +
                    "}" +
                    
                    "@media (max-width: 600px) {" +
                        ".container {" +
                            "margin: 10px;" +
                            "border-radius: 8px;" +
                        "}" +
                        
                        ".header, .content {" +
                            "padding: 25px 20px;" +
                        "}" +
                        
                        ".logo-text {" +
                            "font-size: 24px;" +
                        "}" +
                        
                        ".greeting {" +
                            "font-size: 20px;" +
                        "}" +
                        
                        ".cta-button {" +
                            "padding: 12px 24px;" +
                            "font-size: 15px;" +
                        "}" +
                    "}" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"container\">" +
                    "<div class=\"header\">" +
                        "<div class=\"logo\">" +
                            "<div class=\"logo-icon\">💡</div>" +
                            "<h1 class=\"logo-text\">SkillLink</h1>" +
                        "</div>" +
                        "<p class=\"header-subtitle\">Plataforma Emprendedora</p>" +
                    "</div>" +
                    
                    "<div class=\"content\">" +
                        "<div class=\"greeting\">¡Hola " + nombreUsuario + "!</div>" +
                        
                        "<div class=\"message\">" +
                            "Recibimos una solicitud para restablecer la contraseña de tu cuenta en SkillLink. " +
                            "Si fuiste tú quien solicitó este cambio, haz clic en el botón de abajo para crear una nueva contraseña. " +
                            "Este proceso es completamente seguro y te permitirá acceder nuevamente a tu cuenta." +
                        "</div>" +
                        
                        "<div class=\"cta-container\">" +
                            "<a href=\"" + enlaceRecuperacion + "\" class=\"cta-button\">" +
                                "Restablecer contraseña" +
                            "</a>" +
                        "</div>" +
                        
                        "<div class=\"security-notice\">" +
                            "<div class=\"security-title\">Información de seguridad</div>" +
                            "<div class=\"security-text\">" +
                                "Este enlace de recuperación expirará automáticamente en 15 minutos por tu seguridad. " +
                                "Si no solicitaste este cambio de contraseña, puedes ignorar este correo electrónico de forma segura. " +
                                "Tu cuenta permanecerá protegida y no se realizarán cambios. " +
                                "Por tu seguridad, nunca compartas este enlace con otras personas." +
                            "</div>" +
                        "</div>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
}