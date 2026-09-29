package br.edu.ufersa.pw.todo.buggytrip.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pessoas", schema = "bt", uniqueConstraints = @UniqueConstraint(name = "uk_pessoa_cpf", columnNames = "pes_cpf"))
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pes_id")
    Long id;
    @Column(name = "pes_cpf", nullable = false, length = 11)
    String cpf;
    @Column(name = "pes_nome_completo", nullable = false, length = 128)
    String nomeCompleto;
    @Column(name = "pes_data_nascimento", nullable = false)
    LocalDate dataNascimento;
    @Column(name = "pes_data_criacao", nullable = false)
    OffsetDateTime dataCriacao;
    @Column(name = "pes_data_atualizacao", nullable = false)
    OffsetDateTime dataAtualizacao;
    @Version
    @Column(name = "pes_versao", nullable = false)
    Integer numeroVersao;

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

    public String getCpf() {
        return cpf;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setCpf(String v) {
        cpf = v;
    }

    public void setNomeCompleto(String v) {
        nomeCompleto = v;
    }

    public void setDataNascimento(LocalDate v) {
        dataNascimento = v;
    }
}
