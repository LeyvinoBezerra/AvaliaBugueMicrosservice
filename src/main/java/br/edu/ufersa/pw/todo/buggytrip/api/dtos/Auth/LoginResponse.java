package br.edu.ufersa.pw.todo.buggytrip.api.dtos.Auth;

import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;

public record LoginResponse(String token, String type, long expiresInSeconds, Long userId, String nome,
                            EnumUsuario tipo) {
}
