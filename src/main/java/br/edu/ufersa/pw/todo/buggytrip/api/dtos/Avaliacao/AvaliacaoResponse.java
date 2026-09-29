package br.edu.ufersa.pw.todo.buggytrip.api.dtos.Avaliacao;

import java.time.OffsetDateTime;

public record AvaliacaoResponse(Long id, int seguranca, int conhecimentoRoteiro, int confortoVeiculo,
                                int simpatiaMotorista, int experienciaGeral, int adaptabilidade,
                                int paradasInteressantes, String diferencial, String feedback, Long avaliadorId,
                                Long bugueiroId, OffsetDateTime dataCriacao, OffsetDateTime dataAtualizacao,
                                double notaMedia) {
}
