package br.edu.ufersa.pw.todo.buggytrip.domain.service;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Auth.*;
import br.edu.ufersa.pw.todo.buggytrip.api.exceptions.DomainException;
import br.edu.ufersa.pw.todo.buggytrip.infrastructure.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        var usuario = usuarioService.getByEmail(loginRequest.email());
        if (!usuario.isAtivo() || !passwordEncoder.matches(loginRequest.senha(), usuario.getSenha())) {
            logger.warn("Authentication rejected userId={} active={}", usuario.getId(), usuario.isAtivo());
            throw new DomainException("Credenciais inválidas");
        }
        logger.info("User authenticated userId={} role={}", usuario.getId(), usuario.getUsuarioTipo());
        return new LoginResponse(jwtService.generate(usuario), "Bearer", jwtService.expiration(),
                usuario.getId(), usuario.getNome(), usuario.getUsuarioTipo());
    }
}
