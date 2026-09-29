package br.edu.ufersa.pw.todo.buggytrip.infrastructure.config;

import br.edu.ufersa.pw.todo.buggytrip.domain.entities.Usuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("local")
public class DataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seed(UsuarioRepository r, PasswordEncoder e) {
        return a -> {
            c(r, e, "Administrador", "admin@buggytrip.local", "Admin@123", EnumUsuario.ADMIN);
            c(r, e, "Cliente Demo", "cliente@buggytrip.local", "Cliente@123", EnumUsuario.CLIENTE);
            c(r, e, "Bugueiro Demo", "bugueiro@buggytrip.local", "Bugueiro@123", EnumUsuario.BUGUEIRO);
        };
    }

    void c(UsuarioRepository r, PasswordEncoder e, String n, String m, String p, EnumUsuario t) {
        if (r.existsByEmailIgnoreCase(m)) return;
        var u = new Usuario();
        u.setNome(n);
        u.setEmail(m);
        u.setSenha(e.encode(p));
        u.setUsuarioTipo(t);
        u.setAtivo(true);
        var saved = r.save(u);
        logger.info("Demo user initialized userId={} role={}", saved.getId(), saved.getUsuarioTipo());
    }
}
