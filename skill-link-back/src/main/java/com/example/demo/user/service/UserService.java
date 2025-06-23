package com.example.demo.user.service;

import com.example.demo.user.dto.UserRegisterRequest;
import com.example.demo.user.model.User;
import com.example.demo.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public User register(UserRegisterRequest userRegisterRequest) {
        // Verificar si el email ya existe
        if(userRepository.existsByEmail(userRegisterRequest.email())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear usuario
        String hashedPassword = passwordEncoder.encode(userRegisterRequest.password());
        User user = new User(userRegisterRequest);
        user.setPassword(hashedPassword);
        
        User savedUser = userRepository.save(user);

        // Enviar correo de bienvenida (no bloqueante)
        try {
            String nombreCompleto = savedUser.getName() + " " + savedUser.getSecondName();
            emailService.enviarCorreoBienvenida(savedUser.getEmail(), nombreCompleto);
        } catch (Exception e) {
            System.err.println("⚠️ Error al enviar correo de bienvenida (no crítico): " + e.getMessage());
            // No afectamos el registro del usuario
        }

        return savedUser;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }
        return user;
    }
}