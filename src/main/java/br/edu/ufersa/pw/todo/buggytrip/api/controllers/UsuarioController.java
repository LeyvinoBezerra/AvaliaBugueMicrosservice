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
    final UsuarioService service;

    public UsuarioController(UsuarioService s) {
        service = s;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(r));
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id, Authentication a) {
        selfOrAdmin(id, a);
        return service.buscar(id);
    }

    @GetMapping
    public PageResponse<UsuarioResponse> listar(@PageableDefault(size = 20, sort = "id") Pageable p, Authentication a) {
        admin(a);
        return service.listar(p);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest r, Authentication a) {
        selfOrAdmin(id, a);
        if (a.getAuthorities().stream().noneMatch(x -> x.getAuthority().equals("ROLE_ADMIN"))) {
            var atual = service.get(id);
            r = new UsuarioRequest(r.nome(), r.email(), r.senha(), atual.getUsuarioTipo());
        }
        return service.atualizar(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, Authentication a) {
        selfOrAdmin(id, a);
        service.remover(id);
    }

    private void admin(Authentication a) {
        if (a.getAuthorities().stream().noneMatch(x -> x.getAuthority().equals("ROLE_ADMIN")))
            throw new AccessDeniedException("Apenas ADMIN pode listar usuários");
    }

    private void selfOrAdmin(Long id, Authentication a) {
        if (a.getAuthorities().stream().noneMatch(x -> x.getAuthority().equals("ROLE_ADMIN")) && !String.valueOf(a.getDetails()).equals(String.valueOf(id)))
            throw new AccessDeniedException("Acesso negado");
    }
}
