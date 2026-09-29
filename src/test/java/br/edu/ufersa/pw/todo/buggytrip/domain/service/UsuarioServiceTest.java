package br.edu.ufersa.pw.todo.buggytrip.domain.service;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Usuario.UsuarioRequest;
import br.edu.ufersa.pw.todo.buggytrip.api.exceptions.ConflictException;
import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.repositories.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    @Mock
    UsuarioRepository repo;
    @Mock
    PasswordEncoder encoder;
    UsuarioService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UsuarioService(repo, encoder);
    }

    @Test
    void criaUsuario() {
        var r = new UsuarioRequest("Ana", "ana@test.com", "Senha@123", EnumUsuario.CLIENTE);
        when(repo.existsByEmailIgnoreCase(r.email())).thenReturn(false);
        when(encoder.encode(r.senha())).thenReturn("hash");
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        var x = service.criar(r);
        assertEquals("ana@test.com", x.email());
        assertEquals(EnumUsuario.CLIENTE, x.tipo());
    }

    @Test
    void rejeitaEmailDuplicado() {
        var r = new UsuarioRequest("Ana", "ana@test.com", "Senha@123", EnumUsuario.CLIENTE);
        when(repo.existsByEmailIgnoreCase(r.email())).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.criar(r));
    }

    @Test
    void usaOrdenacaoSeguraQuandoCampoDeOrdenacaoEInvalido() {
        when(repo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.listar(PageRequest.of(0, 1, Sort.by("[\"string\"]")));

        var pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findAll(pageable.capture());
        assertEquals(1, pageable.getValue().getPageSize());
        assertEquals("id", pageable.getValue().getSort().getOrderFor("id").getProperty());
    }

    @Test
    void preservaOrdenacaoValida() {
        var requested = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "nome"));
        when(repo.findAll(requested)).thenReturn(new PageImpl<>(List.of()));

        service.listar(requested);

        verify(repo).findAll(requested);
    }
}
