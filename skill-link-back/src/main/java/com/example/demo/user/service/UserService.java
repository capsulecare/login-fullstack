package com.example.demo.user.service;

import com.example.demo.user.dto.UserRegisterRequest;
import com.example.demo.user.model.User;
import com.example.demo.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(UserRegisterRequest userRegisterRequest) {
        String hashedPassword = passwordEncoder.encode(userRegisterRequest.password());
        User user = new User(userRegisterRequest);
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

}
