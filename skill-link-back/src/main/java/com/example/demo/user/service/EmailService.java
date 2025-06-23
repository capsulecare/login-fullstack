package com.example.demo.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void enviarCorreoRecuperacion(String destinatario, String nombreUsuario, String token) {
        try {
            System.out.println("📧 Iniciando envío de correo de recuperación...");
            System.out.println("   - Destinatario: " + destinatario);
            System.out.println("   - Token: " + token);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "SkillLink - Plataforma Emprendedora");
            helper.setTo(destinatario);
            helper.setSubject("🔐 Recuperación de Contraseña - SkillLink");

            String enlaceRecuperacion = frontendUrl + "/reset-password?token=" + token;
            String contenidoHtml = construirPlantillaHtml(nombreUsuario, enlaceRecuperacion);

            helper.setText(contenidoHtml, true);

            mailSender.send(message);

            System.out.println("✅ Correo de recuperación enviado exitosamente a: " + destinatario);
            System.out.println("🔗 Enlace generado: " + enlaceRecuperacion);

        } catch (Exception e) {
            System.err.println("❌ Error al enviar correo de recuperación a: " + destinatario);
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el correo de recuperación: " + e.getMessage());
        }
    }

    private String construirPlantillaHtml(String nombreUsuario, String enlaceRecuperacion) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Recuperación de Contraseña - SkillLink</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        padding: 20px;
                    }
                    
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        backdrop-filter: blur(10px);
                        border-radius: 20px;
                        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                        border: 1px solid rgba(255, 255, 255, 0.2);
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #10b981 0%, #059669 50%, #7c3aed 100%);
                        padding: 40px 30px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .header::before {
                        content: '';
                        position: absolute;
                        top: -50%;
                        left: -50%;
                        width: 200%;
                        height: 200%;
                        background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
                        animation: float 6s ease-in-out infinite;
                    }
                    
                    @keyframes float {
                        0%, 100% { transform: translateY(0px) rotate(0deg); }
                        50% { transform: translateY(-20px) rotate(180deg); }
                    }
                    
                    .logo {
                        display: inline-flex;
                        align-items: center;
                        gap: 12px;
                        margin-bottom: 20px;
                        position: relative;
                        z-index: 2;
                    }
                    
                    .logo-icon {
                        width: 50px;
                        height: 50px;
                        background: rgba(255, 255, 255, 0.2);
                        border-radius: 12px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        backdrop-filter: blur(10px);
                        border: 1px solid rgba(255, 255, 255, 0.3);
                    }
                    
                    .logo-text {
                        color: white;
                        font-size: 32px;
                        font-weight: 700;
                        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }
                    
                    .header-subtitle {
                        color: rgba(255, 255, 255, 0.9);
                        font-size: 16px;
                        font-weight: 500;
                        position: relative;
                        z-index: 2;
                    }
                    
                    .content {
                        padding: 40px 30px;
                        background: white;
                    }
                    
                    .greeting {
                        font-size: 24px;
                        font-weight: 600;
                        color: #1f2937;
                        margin-bottom: 20px;
                        text-align: center;
                    }
                    
                    .message {
                        font-size: 16px;
                        color: #6b7280;
                        margin-bottom: 30px;
                        text-align: center;
                        line-height: 1.7;
                    }
                    
                    .cta-container {
                        text-align: center;
                        margin: 40px 0;
                    }
                    
                    .cta-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #10b981 0%, #059669 50%, #7c3aed 100%);
                        color: white;
                        text-decoration: none;
                        padding: 16px 32px;
                        border-radius: 12px;
                        font-weight: 600;
                        font-size: 16px;
                        box-shadow: 0 10px 25px rgba(16, 185, 129, 0.3);
                        transition: all 0.3s ease;
                        border: none;
                        cursor: pointer;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .cta-button::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%;
                        width: 100%;
                        height: 100%;
                        background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
                        transition: left 0.5s;
                    }
                    
                    .cta-button:hover::before {
                        left: 100%;
                    }
                    
                    .cta-button:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 15px 35px rgba(16, 185, 129, 0.4);
                    }
                    
                    .security-notice {
                        background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
                        border: 1px solid #f59e0b;
                        border-radius: 12px;
                        padding: 20px;
                        margin: 30px 0;
                        position: relative;
                    }
                    
                    .security-notice::before {
                        content: '🔒';
                        position: absolute;
                        top: -10px;
                        left: 20px;
                        background: #f59e0b;
                        color: white;
                        width: 30px;
                        height: 30px;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 14px;
                    }
                    
                    .security-title {
                        font-weight: 600;
                        color: #92400e;
                        margin-bottom: 8px;
                        margin-top: 10px;
                    }
                    
                    .security-text {
                        color: #b45309;
                        font-size: 14px;
                        line-height: 1.5;
                    }
                    
                    .footer {
                        background: #f9fafb;
                        padding: 30px;
                        text-align: center;
                        border-top: 1px solid #e5e7eb;
                    }
                    
                    .footer-text {
                        color: #6b7280;
                        font-size: 14px;
                        margin-bottom: 15px;
                    }
                    
                    .footer-links {
                        display: flex;
                        justify-content: center;
                        gap: 20px;
                        flex-wrap: wrap;
                    }
                    
                    .footer-link {
                        color: #7c3aed;
                        text-decoration: none;
                        font-size: 14px;
                        font-weight: 500;
                        transition: color 0.3s ease;
                    }
                    
                    .footer-link:hover {
                        color: #5b21b6;
                    }
                    
                    .divider {
                        height: 1px;
                        background: linear-gradient(90deg, transparent, #e5e7eb, transparent);
                        margin: 30px 0;
                    }
                    
                    @media (max-width: 600px) {
                        .container {
                            margin: 10px;
                            border-radius: 15px;
                        }
                        
                        .header, .content, .footer {
                            padding: 25px 20px;
                        }
                        
                        .logo-text {
                            font-size: 28px;
                        }
                        
                        .greeting {
                            font-size: 22px;
                        }
                        
                        .cta-button {
                            padding: 14px 28px;
                            font-size: 15px;
                        }
                        
                        .footer-links {
                            flex-direction: column;
                            gap: 10px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">
                            <div class="logo-icon">💡</div>
                            <div class="logo-text">SkillLink</div>
                        </div>
                        <div class="header-subtitle">Plataforma Emprendedora</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">¡Hola %s!</div>
                        
                        <div class="message">
                            Recibimos una solicitud para restablecer la contraseña de tu cuenta en SkillLink. 
                            Si fuiste tú quien solicitó este cambio, haz clic en el botón de abajo para crear una nueva contraseña.
                        </div>
                        
                        <div class="cta-container">
                            <a href="%s" class="cta-button">
                                🔐 Restablecer mi contraseña
                            </a>
                        </div>
                        
                        <div class="security-notice">
                            <div class="security-title">Información de seguridad</div>
                            <div class="security-text">
                                • Este enlace expirará en 15 minutos por tu seguridad<br>
                                • Si no solicitaste este cambio, puedes ignorar este correo<br>
                                • Nunca compartas este enlace con otras personas
                            </div>
                        </div>
                        
                        <div class="divider"></div>
                        
                        <div class="message" style="font-size: 14px; color: #9ca3af;">
                            Si el botón no funciona, copia y pega este enlace en tu navegador:<br>
                            <a href="%s" style="color: #7c3aed; word-break: break-all;">%s</a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                            Este correo fue enviado desde SkillLink - Plataforma Emprendedora
                        </div>
                        <div class="footer-links">
                            <a href="#" class="footer-link">Centro de Ayuda</a>
                            <a href="#" class="footer-link">Política de Privacidad</a>
                            <a href="#" class="footer-link">Términos de Servicio</a>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombreUsuario, enlaceRecuperacion, enlaceRecuperacion, enlaceRecuperacion);
    }

    public void enviarCorreoBienvenida(String destinatario, String nombreUsuario) {
        try {
            System.out.println("📧 Enviando correo de bienvenida a: " + destinatario);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "SkillLink - Plataforma Emprendedora");
            helper.setTo(destinatario);
            helper.setSubject("🎉 ¡Bienvenido a SkillLink! Tu viaje emprendedor comienza aquí");

            String contenidoHtml = construirPlantillaBienvenida(nombreUsuario);
            helper.setText(contenidoHtml, true);

            mailSender.send(message);

            System.out.println("✅ Correo de bienvenida enviado exitosamente a: " + destinatario);

        } catch (Exception e) {
            System.err.println("❌ Error al enviar correo de bienvenida a: " + destinatario);
            System.err.println("   Error: " + e.getMessage());
            // No lanzamos excepción para que no afecte el registro del usuario
        }
    }

    private String construirPlantillaBienvenida(String nombreUsuario) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>¡Bienvenido a SkillLink!</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        padding: 20px;
                    }
                    
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        backdrop-filter: blur(10px);
                        border-radius: 20px;
                        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                        border: 1px solid rgba(255, 255, 255, 0.2);
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #10b981 0%, #059669 50%, #7c3aed 100%);
                        padding: 40px 30px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .header::before {
                        content: '';
                        position: absolute;
                        top: -50%;
                        left: -50%;
                        width: 200%;
                        height: 200%;
                        background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
                        animation: float 6s ease-in-out infinite;
                    }
                    
                    @keyframes float {
                        0%, 100% { transform: translateY(0px) rotate(0deg); }
                        50% { transform: translateY(-20px) rotate(180deg); }
                    }
                    
                    .logo {
                        display: inline-flex;
                        align-items: center;
                        gap: 12px;
                        margin-bottom: 20px;
                        position: relative;
                        z-index: 2;
                    }
                    
                    .logo-icon {
                        width: 50px;
                        height: 50px;
                        background: rgba(255, 255, 255, 0.2);
                        border-radius: 12px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        backdrop-filter: blur(10px);
                        border: 1px solid rgba(255, 255, 255, 0.3);
                    }
                    
                    .logo-text {
                        color: white;
                        font-size: 32px;
                        font-weight: 700;
                        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }
                    
                    .header-subtitle {
                        color: rgba(255, 255, 255, 0.9);
                        font-size: 16px;
                        font-weight: 500;
                        position: relative;
                        z-index: 2;
                    }
                    
                    .content {
                        padding: 40px 30px;
                        background: white;
                    }
                    
                    .greeting {
                        font-size: 28px;
                        font-weight: 600;
                        color: #1f2937;
                        margin-bottom: 20px;
                        text-align: center;
                    }
                    
                    .welcome-message {
                        font-size: 16px;
                        color: #6b7280;
                        margin-bottom: 30px;
                        text-align: center;
                        line-height: 1.7;
                    }
                    
                    .features {
                        margin: 40px 0;
                    }
                    
                    .feature {
                        display: flex;
                        align-items: center;
                        margin-bottom: 20px;
                        padding: 15px;
                        background: #f9fafb;
                        border-radius: 12px;
                        border-left: 4px solid #10b981;
                    }
                    
                    .feature-icon {
                        font-size: 24px;
                        margin-right: 15px;
                        width: 40px;
                        text-align: center;
                    }
                    
                    .feature-text {
                        flex: 1;
                    }
                    
                    .feature-title {
                        font-weight: 600;
                        color: #1f2937;
                        margin-bottom: 5px;
                    }
                    
                    .feature-description {
                        color: #6b7280;
                        font-size: 14px;
                    }
                    
                    .cta-container {
                        text-align: center;
                        margin: 40px 0;
                    }
                    
                    .cta-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #10b981 0%, #059669 50%, #7c3aed 100%);
                        color: white;
                        text-decoration: none;
                        padding: 16px 32px;
                        border-radius: 12px;
                        font-weight: 600;
                        font-size: 16px;
                        box-shadow: 0 10px 25px rgba(16, 185, 129, 0.3);
                        transition: all 0.3s ease;
                        border: none;
                        cursor: pointer;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .cta-button::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%;
                        width: 100%;
                        height: 100%;
                        background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
                        transition: left 0.5s;
                    }
                    
                    .cta-button:hover::before {
                        left: 100%;
                    }
                    
                    .cta-button:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 15px 35px rgba(16, 185, 129, 0.4);
                    }
                    
                    .footer {
                        background: #f9fafb;
                        padding: 30px;
                        text-align: center;
                        border-top: 1px solid #e5e7eb;
                    }
                    
                    .footer-text {
                        color: #6b7280;
                        font-size: 14px;
                        margin-bottom: 15px;
                    }
                    
                    .footer-links {
                        display: flex;
                        justify-content: center;
                        gap: 20px;
                        flex-wrap: wrap;
                    }
                    
                    .footer-link {
                        color: #7c3aed;
                        text-decoration: none;
                        font-size: 14px;
                        font-weight: 500;
                        transition: color 0.3s ease;
                    }
                    
                    .footer-link:hover {
                        color: #5b21b6;
                    }
                    
                    @media (max-width: 600px) {
                        .container {
                            margin: 10px;
                            border-radius: 15px;
                        }
                        
                        .header, .content, .footer {
                            padding: 25px 20px;
                        }
                        
                        .logo-text {
                            font-size: 28px;
                        }
                        
                        .greeting {
                            font-size: 24px;
                        }
                        
                        .cta-button {
                            padding: 14px 28px;
                            font-size: 15px;
                        }
                        
                        .footer-links {
                            flex-direction: column;
                            gap: 10px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">
                            <div class="logo-icon">💡</div>
                            <div class="logo-text">SkillLink</div>
                        </div>
                        <div class="header-subtitle">Plataforma Emprendedora</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">¡Bienvenido %s! 🎉</div>
                        
                        <div class="welcome-message">
                            Te damos la bienvenida a SkillLink, la plataforma donde los emprendedores transforman 
                            sus ideas en realidades exitosas. Estamos emocionados de tenerte en nuestra comunidad.
                        </div>
                        
                        <div class="features">
                            <div class="feature">
                                <div class="feature-icon">🚀</div>
                                <div class="feature-text">
                                    <div class="feature-title">Desarrolla tu MVP</div>
                                    <div class="feature-description">Herramientas y metodologías para crear tu producto mínimo viable</div>
                                </div>
                            </div>
                            
                            <div class="feature">
                                <div class="feature-icon">👥</div>
                                <div class="feature-text">
                                    <div class="feature-title">Conecta con mentores</div>
                                    <div class="feature-description">Acceso a expertos que te guiarán en tu viaje emprendedor</div>
                                </div>
                            </div>
                            
                            <div class="feature">
                                <div class="feature-icon">🤝</div>
                                <div class="feature-text">
                                    <div class="feature-title">Forma tu equipo</div>
                                    <div class="feature-description">Encuentra colaboradores con habilidades complementarias</div>
                                </div>
                            </div>
                            
                            <div class="feature">
                                <div class="feature-icon">📊</div>
                                <div class="feature-text">
                                    <div class="feature-title">Valida tu idea</div>
                                    <div class="feature-description">Obtén feedback valioso de la comunidad e inversores</div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="cta-container">
                            <a href="%s/home" class="cta-button">
                                🌟 Comenzar mi viaje emprendedor
                            </a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                            ¡Gracias por unirte a SkillLink! Estamos aquí para apoyarte en cada paso.
                        </div>
                        <div class="footer-links">
                            <a href="#" class="footer-link">Centro de Ayuda</a>
                            <a href="#" class="footer-link">Comunidad</a>
                            <a href="#" class="footer-link">Recursos</a>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombreUsuario, frontendUrl);
    }
}