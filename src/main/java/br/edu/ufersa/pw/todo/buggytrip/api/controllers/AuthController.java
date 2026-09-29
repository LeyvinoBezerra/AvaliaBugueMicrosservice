package br.edu.ufersa.pw.todo.buggytrip.api.controllers;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Auth.*;
import br.edu.ufersa.pw.todo.buggytrip.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação")
public class AuthController {
    final AuthService service;

    public AuthController(AuthService s) {
        service = s;
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica usuário e retorna JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest r) {
        return ResponseEntity.ok(service.login(r));
    }
}
