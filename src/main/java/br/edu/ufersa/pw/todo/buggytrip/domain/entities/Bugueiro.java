package br.edu.ufersa.pw.todo.buggytrip.domain.entities;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "bugueiros", schema = "bt", uniqueConstraints = @UniqueConstraint(name = "uk_bugueiro_email", columnNames = "bug_email"))
public class Bugueiro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bug_id")
    Long id;
    @Column(name = "bug_nome", nullable = false, length = 100)
    String nome;
    @Column(name = "bug_email", nullable = false, length = 150)
    String email;
    @Column(name = "bug_telefone", length = 20)
    String telefone;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bug_perfil_id", nullable = false)
    Perfil perfil;
    @Column(name = "bug_data_criacao", nullable = false)
    OffsetDateTime dataCriacao;
    @Column(name = "bug_data_atualizacao", nullable = false)
    OffsetDateTime dataAtualizacao;
    @Version
    @Column(name = "bug_versao", nullable = false)
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

    public void setNome(String v) {
        nome = v;
    }

    public void setEmail(String v) {
        email = v;
    }

    public void setTelefone(String v) {
        telefone = v;
    }

    public void setPerfil(Perfil v) {
        perfil = v;
    }
}
