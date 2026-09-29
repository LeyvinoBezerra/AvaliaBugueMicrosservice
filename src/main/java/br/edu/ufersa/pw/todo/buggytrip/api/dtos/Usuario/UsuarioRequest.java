package br.edu.ufersa.pw.todo.buggytrip.api.dtos.Usuario;

import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import jakarta.validation.constraints.*;

public record UsuarioRequest(@NotBlank @Size(max = 100) String nome, @NotBlank @Email @Size(max = 150) String email,
                             @NotBlank @Size(min = 8, max = 100) String senha, @NotNull EnumUsuario tipo) {
}
