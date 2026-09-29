package br.edu.ufersa.pw.todo.buggytrip.domain.service;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Pagination.PageResponse;
import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Usuario.*;
import br.edu.ufersa.pw.todo.buggytrip.api.exceptions.*;
import br.edu.ufersa.pw.todo.buggytrip.domain.entities.Usuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UsuarioService {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id", "nome", "email", "usuarioTipo", "ativo", "dataCriacao", "dataAtualizacao");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest usuarioRequest) {
        if (usuarioRequest.tipo() != br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario.CLIENTE)
            throw new BusinessRuleException("Cadastro público permite somente CLIENTE");
        if (usuarioRepository.existsByEmailIgnoreCase(usuarioRequest.email()))
            throw new ConflictException("E-mail já cadastrado");
        var usuario = new Usuario();
        usuario.setNome(usuarioRequest.nome().trim());
        usuario.setEmail(usuarioRequest.email().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(usuarioRequest.senha()));
        usuario.setUsuarioTipo(usuarioRequest.tipo());
        usuario.setAtivo(true);
        var usuarioSalvo = usuarioRepository.save(usuario);
        logger.info("User created userId={} role={}", usuarioSalvo.getId(), usuarioSalvo.getUsuarioTipo());
        return toResponse(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponse atualizar(Long usuarioId, UsuarioRequest usuarioRequest) {
        var usuario = get(usuarioId);
        var usuarioComMesmoEmail = usuarioRepository.findByEmailIgnoreCase(usuarioRequest.email());
        if (usuarioComMesmoEmail.isPresent() && !usuarioComMesmoEmail.get().getId().equals(usuarioId))
            throw new ConflictException("E-mail já cadastrado");
        usuario.setNome(usuarioRequest.nome().trim());
        usuario.setEmail(usuarioRequest.email().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(usuarioRequest.senha()));
        usuario.setUsuarioTipo(usuarioRequest.tipo());
        var usuarioSalvo = usuarioRepository.save(usuario);
        logger.info("User updated userId={} role={}", usuarioSalvo.getId(), usuarioSalvo.getUsuarioTipo());
        return toResponse(usuarioSalvo);
    }

    @Transactional
    public void remover(Long usuarioId) {
        var usuario = get(usuarioId);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        logger.info("User deactivated userId={}", usuarioId);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long usuarioId) {
        return toResponse(get(usuarioId));
    }

    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> listar(Pageable pageable) {
        var safePageable = validarOrdenacao(pageable);
        var usuariosPage = usuarioRepository.findAll(safePageable).map(this::toResponse);
        return new PageResponse<>(usuariosPage.getContent(), usuariosPage.getNumber(), usuariosPage.getSize(),
                usuariosPage.getTotalElements(), usuariosPage.getTotalPages(), usuariosPage.isFirst(), usuariosPage.isLast());
    }

    private Pageable validarOrdenacao(Pageable pageable) {
        if (pageable.isUnpaged() || pageable.getSort().isUnsorted()) {
            return pageable;
        }
        if (pageable.getSort().stream().anyMatch(order -> !SORTABLE_FIELDS.contains(order.getProperty()))) {
            logger.warn("Unsupported user sort field; using id sort");
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
        }
        return pageable;
    }

    @Transactional(readOnly = true)
    public Usuario get(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + usuarioId));
    }

    @Transactional(readOnly = true)
    public Usuario getByEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getUsuarioTipo(), usuario.isAtivo());
    }
}
