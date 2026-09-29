package br.edu.ufersa.pw.todo.buggytrip.domain.service;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Auth.*;
import br.edu.ufersa.pw.todo.buggytrip.api.exceptions.DomainException;
import br.edu.ufersa.pw.todo.buggytrip.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    final UsuarioService users;
    final PasswordEncoder encoder;
    final JwtService jwt;

    public AuthService(UsuarioService u, PasswordEncoder e, JwtService j) {
        users = u;
        encoder = e;
        jwt = j;
    }

    public LoginResponse login(LoginRequest r) {
        var u = users.getByEmail(r.email());
        if (!u.isAtivo() || !encoder.matches(r.senha(), u.getSenha()))
            throw new DomainException("Credenciais inválidas");
        return new LoginResponse(jwt.generate(u), "Bearer", jwt.expiration(), u.getId(), u.getNome(), u.getUsuarioTipo());
    }
}
