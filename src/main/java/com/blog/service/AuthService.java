package com.blog.service;

import com.blog.config.JwtUtil;
import com.blog.dto.LoginRequest;
import com.blog.dto.LoginResponse;
import com.blog.exception.UnauthorizedException;
import com.blog.model.Usuario;
import com.blog.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {


    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse autenticar(LoginRequest request) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(request.getEmail());

        if (usuario.isEmpty() || !passwordEncoder.matches(request.getPassword(), usuario.get().getPassword())) {
            throw new UnauthorizedException("E-mail ou senha incorretos");
        }

        String token = jwtUtil.gerarToken(usuario.get().getEmail());
        return new LoginResponse(token);
    }
}