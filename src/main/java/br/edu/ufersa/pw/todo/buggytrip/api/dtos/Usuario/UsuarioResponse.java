package br.edu.ufersa.pw.todo.buggytrip.api.dtos.Usuario;

import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;

public record UsuarioResponse(Long id, String nome, String email, EnumUsuario tipo, boolean ativo) {
}
