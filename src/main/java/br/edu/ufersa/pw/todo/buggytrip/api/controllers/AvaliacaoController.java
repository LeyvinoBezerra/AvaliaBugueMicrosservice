package br.edu.ufersa.pw.todo.buggytrip.api.controllers;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Avaliacao.*;
import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Pagination.PageResponse;
import br.edu.ufersa.pw.todo.buggytrip.domain.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/avaliacoes")
@Tag(name = "Avaliações")
public class AvaliacaoController {
    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public PageResponse<AvaliacaoResponse> listar(
            @RequestParam(required = false) Long bugueiroId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return avaliacaoService.listar(bugueiroId, pageable);
    }

    @GetMapping("/{id}")
    public AvaliacaoResponse buscar(@PathVariable("id") Long avaliacaoId) {
        return avaliacaoService.buscar(avaliacaoId);
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponse> criar(
            @Valid @RequestBody AvaliacaoRequest avaliacaoRequest,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoService.criar(
                avaliacaoRequest, authenticatedUserId(authentication), isAdministrator(authentication)));
    }

    @PutMapping("/{id}")
    public AvaliacaoResponse atualizar(
            @PathVariable("id") Long avaliacaoId,
            @Valid @RequestBody AvaliacaoRequest avaliacaoRequest,
            Authentication authentication) {
        return avaliacaoService.atualizar(avaliacaoId, avaliacaoRequest,
                authenticatedUserId(authentication), isAdministrator(authentication));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable("id") Long avaliacaoId, Authentication authentication) {
        avaliacaoService.remover(avaliacaoId, authenticatedUserId(authentication), isAdministrator(authentication));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(String.valueOf(authentication.getDetails()));
    }

    private boolean isAdministrator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
