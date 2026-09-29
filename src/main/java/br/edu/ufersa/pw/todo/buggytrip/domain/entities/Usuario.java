package br.edu.ufersa.pw.todo.buggytrip.domain.entities;

import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "usuarios", schema = "bt", uniqueConstraints = @UniqueConstraint(name = "uk_usuarios_email", columnNames = "usu_email"))
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usu_id")
    Long id;
    @Column(name = "usu_nome", nullable = false, length = 100)
    String nome;
    @Column(name = "usu_email", nullable = false, length = 150)
    String email;
    @Column(name = "usu_senha", nullable = false, length = 255)
    String senha;
    @Enumerated(EnumType.STRING)
    @Column(name = "usu_tipo", nullable = false, length = 20)
    EnumUsuario usuarioTipo;
    @Column(name = "usu_ativo", nullable = false)
    boolean ativo = true;
    @Column(name = "usu_data_criacao", nullable = false)
    OffsetDateTime dataCriacao;
    @Column(name = "usu_data_atualizacao", nullable = false)
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

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public EnumUsuario getUsuarioTipo() {
        return usuarioTipo;
    }

    public boolean isAtivo() {
        return ativo;
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

    public void setNome(String v) {
        nome = v;
    }

    public void setEmail(String v) {
        email = v;
    }

    public void setSenha(String v) {
        senha = v;
    }

    public void setUsuarioTipo(EnumUsuario v) {
        usuarioTipo = v;
    }

    public void setAtivo(boolean v) {
        ativo = v;
    }
}
