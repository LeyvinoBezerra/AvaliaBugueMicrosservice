package br.edu.ufersa.pw.todo.buggytrip.api.controllers;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Pagination.PageResponse;
import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Usuario.*;
import br.edu.ufersa.pw.todo.buggytrip.domain.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(usuarioRequest));
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable("id") Long usuarioId, Authentication authentication) {
        requireOwnerOrAdmin(usuarioId, authentication);
        return usuarioService.buscar(usuarioId);
    }

    @GetMapping
    public PageResponse<UsuarioResponse> listar(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            Authentication authentication) {
        requireAdministrator(authentication);
        return usuarioService.listar(pageable);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(
            @PathVariable("id") Long usuarioId,
            @Valid @RequestBody UsuarioRequest usuarioRequest,
            Authentication authentication) {
        requireOwnerOrAdmin(usuarioId, authentication);
        if (!isAdministrator(authentication)) {
            var existingUser = usuarioService.get(usuarioId);
            usuarioRequest = new UsuarioRequest(
                    usuarioRequest.nome(), usuarioRequest.email(), usuarioRequest.senha(), existingUser.getUsuarioTipo());
        }
        return usuarioService.atualizar(usuarioId, usuarioRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable("id") Long usuarioId, Authentication authentication) {
        requireOwnerOrAdmin(usuarioId, authentication);
        usuarioService.remover(usuarioId);
    }

    private void requireAdministrator(Authentication authentication) {
        if (!isAdministrator(authentication))
            throw new AccessDeniedException("Apenas ADMIN pode listar usuários");
    }

    private void requireOwnerOrAdmin(Long usuarioId, Authentication authentication) {
        if (!isAdministrator(authentication)
                && !String.valueOf(authentication.getDetails()).equals(String.valueOf(usuarioId))) {
            throw new AccessDeniedException("Acesso negado");
        }
    }

    private boolean isAdministrator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
