package br.edu.ufersa.pw.todo.buggytrip.api.controllers;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Avaliacao.*;
import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Pagination.PageResponse;
import br.edu.ufersa.pw.todo.buggytrip.domain.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/avaliacoes")
@Tag(name = "Avaliações")
public class AvaliacaoController {
    final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService s) {
        service = s;
    }

    @GetMapping
    public PageResponse<AvaliacaoResponse> listar(@RequestParam(required = false) Long bugueiroId, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable p) {
        return service.listar(bugueiroId, p);
    }

    @GetMapping("/{id}")
    public AvaliacaoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponse> criar(@Valid @RequestBody AvaliacaoRequest r, org.springframework.security.core.Authentication a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(r, Long.valueOf(String.valueOf(a.getDetails())), isAdmin(a)));
    }

    @PutMapping("/{id}")
    public AvaliacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest r, org.springframework.security.core.Authentication a) {
        return service.atualizar(id, r, Long.valueOf(String.valueOf(a.getDetails())), isAdmin(a));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, org.springframework.security.core.Authentication a) {
        service.remover(id, Long.valueOf(String.valueOf(a.getDetails())), isAdmin(a));
    }

    private boolean isAdmin(org.springframework.security.core.Authentication a) {
        return a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
    }
}
