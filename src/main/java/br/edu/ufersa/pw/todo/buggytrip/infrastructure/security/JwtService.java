package br.edu.ufersa.pw.todo.buggytrip.infrastructure.security;

import br.edu.ufersa.pw.todo.buggytrip.domain.entities.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    final SecretKey key;
    final long expiration;

    public JwtService(@Value("${app.security.jwt.secret}") String secret, @Value("${app.security.jwt.expiration-seconds:3600}") long e) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32)
            throw new IllegalArgumentException("JWT secret deve ter ao menos 32 bytes");
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        expiration = e;
    }

    public String generate(Usuario u) {
        var n = Instant.now();
        return Jwts.builder().subject(u.getEmail()).claim("uid", u.getId()).claim("role", u.getUsuarioTipo().name()).issuedAt(Date.from(n)).expiration(Date.from(n.plusSeconds(expiration))).signWith(key).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long expiration() {
        return expiration;
    }
}
