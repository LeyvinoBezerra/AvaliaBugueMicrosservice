package br.edu.ufersa.pw.todo.buggytrip.domain.entities;

import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "perfis", schema = "bt")
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "per_id")
    Long id;
    @Column(name = "per_nome", nullable = false, length = 30)
    String nome;
    @Enumerated(EnumType.STRING)
    @Column(name = "per_permissao", nullable = false, length = 20)
    EnumUsuario permissoes;
    @Column(name = "per_acesso_global", nullable = false)
    Boolean acessoGlobal = false;
    @Column(name = "per_data_criacao", nullable = false)
    OffsetDateTime dataCriacao;
    @Column(name = "per_data_atualizacao", nullable = false)
    OffsetDateTime dataAtualizacao;
    @Version
    @Column(name = "per_numero_versao", nullable = false)
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

    public String getNome() {
        return nome;
    }

    public EnumUsuario getPermissoes() {
        return permissoes;
    }

    public Boolean getAcessoGlobal() {
        return acessoGlobal;
    }

    public void setNome(String v) {
        nome = v;
    }

    public void setPermissoes(EnumUsuario v) {
        permissoes = v;
    }

    public void setAcessoGlobal(Boolean v) {
        acessoGlobal = v;
    }
}
