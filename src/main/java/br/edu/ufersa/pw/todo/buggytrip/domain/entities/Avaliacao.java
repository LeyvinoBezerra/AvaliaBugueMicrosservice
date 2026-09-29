package br.edu.ufersa.pw.todo.buggytrip.domain.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "avaliacoes", schema = "bt", uniqueConstraints = @UniqueConstraint(name = "uk_avaliacao_par", columnNames = {"ava_avaliador_id", "ava_bugueiro_id"}))
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ava_id")
    Long id;
    @Column(name = "ava_seguranca", nullable = false)
    int seguranca;
    @Column(name = "ava_conhecimento_roteiro", nullable = false)
    int conhecimentoRoteiro;
    @Column(name = "ava_conforto_veiculo", nullable = false)
    int confortoVeiculo;
    @Column(name = "ava_simpatia_motorista", nullable = false)
    int simpatiaMotorista;
    @Column(name = "ava_experiencia_geral", nullable = false)
    int experienciaGeral;
    @Column(name = "ava_adaptabilidade", nullable = false)
    int adaptabilidade;
    @Column(name = "ava_paradas_interessantes", nullable = false)
    int paradasInteressantes;
    @Column(name = "ava_diferencial", length = 255)
    String diferencial;
    @Column(name = "ava_feedback", length = 500)
    String feedback;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ava_avaliador_id", nullable = false)
    Usuario avaliador;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ava_bugueiro_id", nullable = false)
    Usuario bugueiro;
    @Column(name = "ava_data_criacao", nullable = false)
    OffsetDateTime dataCriacao;
    @Column(name = "ava_data_atualizacao", nullable = false)
    OffsetDateTime dataAtualizacao;

    @PrePersist
    void p() {
        var n = OffsetDateTime.now();
        dataCriacao = n;
        dataAtualizacao = n;
    }

    @PreUpdate
    void u() {
        dataAtualizacao = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public int getSeguranca() {
        return seguranca;
    }

    public int getConhecimentoRoteiro() {
        return conhecimentoRoteiro;
    }

    public int getConfortoVeiculo() {
        return confortoVeiculo;
    }

    public int getSimpatiaMotorista() {
        return simpatiaMotorista;
    }

    public int getExperienciaGeral() {
        return experienciaGeral;
    }

    public int getAdaptabilidade() {
        return adaptabilidade;
    }

    public int getParadasInteressantes() {
        return paradasInteressantes;
    }

    public String getDiferencial() {
        return diferencial;
    }

    public String getFeedback() {
        return feedback;
    }

    public Usuario getAvaliador() {
        return avaliador;
    }

    public Usuario getBugueiro() {
        return bugueiro;
    }

    public OffsetDateTime getDataCriacao() {
        return dataCriacao;
    }

    public OffsetDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setId(Long v) {
        id = v;
    }

    public void setSeguranca(int v) {
        seguranca = v;
    }

    public void setConhecimentoRoteiro(int v) {
        conhecimentoRoteiro = v;
    }

    public void setConfortoVeiculo(int v) {
        confortoVeiculo = v;
    }

    public void setSimpatiaMotorista(int v) {
        simpatiaMotorista = v;
    }

    public void setExperienciaGeral(int v) {
        experienciaGeral = v;
    }

    public void setAdaptabilidade(int v) {
        adaptabilidade = v;
    }

    public void setParadasInteressantes(int v) {
        paradasInteressantes = v;
    }

    public void setDiferencial(String v) {
        diferencial = v;
    }

    public void setFeedback(String v) {
        feedback = v;
    }

    public void setAvaliador(Usuario v) {
        avaliador = v;
    }

    public void setBugueiro(Usuario v) {
        bugueiro = v;
    }
}
