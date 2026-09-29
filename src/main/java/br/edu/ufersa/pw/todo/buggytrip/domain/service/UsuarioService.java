package br.edu.ufersa.pw.todo.buggytrip.domain.service;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Pagination.PageResponse;
import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Usuario.*;
import br.edu.ufersa.pw.todo.buggytrip.api.exceptions.*;
import br.edu.ufersa.pw.todo.buggytrip.domain.entities.Usuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.repositories.UsuarioRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    final UsuarioRepository repo;
    final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository r, PasswordEncoder e) {
        repo = r;
        encoder = e;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest r) {
        if (r.tipo() != br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario.CLIENTE)
            throw new BusinessRuleException("Cadastro público permite somente CLIENTE");
        if (repo.existsByEmailIgnoreCase(r.email())) throw new ConflictException("E-mail já cadastrado");
        var u = new Usuario();
        u.setNome(r.nome().trim());
        u.setEmail(r.email().trim().toLowerCase());
        u.setSenha(encoder.encode(r.senha()));
        u.setUsuarioTipo(r.tipo());
        u.setAtivo(true);
        return response(repo.save(u));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest r) {
        var u = get(id);
        var other = repo.findByEmailIgnoreCase(r.email());
        if (other.isPresent() && !other.get().getId().equals(id)) throw new ConflictException("E-mail já cadastrado");
        u.setNome(r.nome().trim());
        u.setEmail(r.email().trim().toLowerCase());
        u.setSenha(encoder.encode(r.senha()));
        u.setUsuarioTipo(r.tipo());
        return response(repo.save(u));
    }

    @Transactional
    public void remover(Long id) {
        var u = get(id);
        u.setAtivo(false);
        repo.save(u);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return response(get(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> listar(Pageable p) {
        var x = repo.findAll(p).map(this::response);
        return new PageResponse<>(x.getContent(), x.getNumber(), x.getSize(), x.getTotalElements(), x.getTotalPages(), x.isFirst(), x.isLast());
    }

    @Transactional(readOnly = true)
    public Usuario get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario getByEmail(String email) {
        return repo.findByEmailIgnoreCase(email).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private UsuarioResponse response(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getUsuarioTipo(), u.isAtivo());
    }
}
