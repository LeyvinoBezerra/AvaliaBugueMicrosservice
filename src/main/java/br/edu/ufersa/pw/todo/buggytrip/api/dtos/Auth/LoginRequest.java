package br.edu.ufersa.pw.todo.buggytrip.api.dtos.Auth;

import jakarta.validation.constraints.*;

public record LoginRequest(@Email @NotBlank String email, @NotBlank String senha) {
}
