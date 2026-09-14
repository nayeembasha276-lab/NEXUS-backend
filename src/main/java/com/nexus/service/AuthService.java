package com.nexus.service;

import com.nexus.dto.*;
import com.nexus.entity.User;
import com.nexus.exception.DuplicateResourceException;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.repository.UserRepository;
import com.nexus.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(
            RegisterRequest request
    ) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }


        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }

        User user = User.builder()

                .name(request.getName())

                .email(request.getEmail())

                .username(request.getUsername())

                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )

                .role("USER")

                .build();

        userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return AuthResponse.builder()

                .token(token)

                .username(user.getUsername())

                .message(
                        "Registration successful"
                )

                .build();
    }

    public AuthResponse login(
            LoginRequest request
    ) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getEmail(),

                        request.getPassword()
                )
        );

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );


        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token =
                jwtService.generateToken(
                        userDetails
                );

        return AuthResponse.builder()

                .token(token)

                .username(user.getUsername())

                .message("Login successful")

                .build();
    }
}