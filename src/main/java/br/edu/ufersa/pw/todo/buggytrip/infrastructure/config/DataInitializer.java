package br.edu.ufersa.pw.todo.buggytrip.infrastructure.config;

import br.edu.ufersa.pw.todo.buggytrip.domain.entities.Usuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
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
        r.save(u);
    }
}
