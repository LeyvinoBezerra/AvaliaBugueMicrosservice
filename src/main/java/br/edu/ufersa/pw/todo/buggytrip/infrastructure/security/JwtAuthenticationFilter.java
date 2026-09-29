package br.edu.ufersa.pw.todo.buggytrip.infrastructure.security;

import br.edu.ufersa.pw.todo.buggytrip.domain.repositories.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    final JwtService jwt;
    final UsuarioRepository users;

    public JwtAuthenticationFilter(JwtService j, UsuarioRepository u) {
        jwt = j;
        users = u;
    }

    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) try {
            var c = jwt.parse(h.substring(7));
            users.findByEmailIgnoreCase(c.getSubject()).filter(x -> x.isAtivo()).ifPresent(u -> {
                var a = new UsernamePasswordAuthenticationToken(u.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_" + u.getUsuarioTipo())));
                a.setDetails(u.getId());
                SecurityContextHolder.getContext().setAuthentication(a);
            });
        } catch (JwtException | IllegalArgumentException ignored) {
            logger.debug("JWT authentication rejected reason={}", ignored.getClass().getSimpleName());
        }
        chain.doFilter(req, res);
    }
}
