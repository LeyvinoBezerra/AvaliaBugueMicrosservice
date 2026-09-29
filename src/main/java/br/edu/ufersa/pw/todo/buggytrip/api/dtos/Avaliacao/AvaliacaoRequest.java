package br.edu.ufersa.pw.todo.buggytrip.api.dtos.Avaliacao;

import jakarta.validation.constraints.*;

public record AvaliacaoRequest(@NotNull @Min(0) @Max(10) Integer seguranca,
                               @NotNull @Min(0) @Max(10) Integer conhecimentoRoteiro,
                               @NotNull @Min(0) @Max(10) Integer confortoVeiculo,
                               @NotNull @Min(0) @Max(10) Integer simpatiaMotorista,
                               @NotNull @Min(0) @Max(10) Integer experienciaGeral,
                               @NotNull @Min(0) @Max(10) Integer adaptabilidade,
                               @NotNull @Min(0) @Max(10) Integer paradasInteressantes,
                               @Size(max = 255) String diferencial, @Size(max = 500) String feedback,
                               @NotNull Long avaliadorId, @NotNull Long bugueiroId) {
}
