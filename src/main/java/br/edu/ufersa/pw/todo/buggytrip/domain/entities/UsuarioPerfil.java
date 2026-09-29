package br.edu.ufersa.pw.todo.buggytrip.domain.entities;

import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "usuarios_perfis", schema = "bt", uniqueConstraints = @UniqueConstraint(name = "uk_usuario_perfil", columnNames = {"up_usu_id", "up_per_id"}))
public class UsuarioPerfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "up_id")
    Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "up_usu_id", nullable = false)
    Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "up_per_id", nullable = false)
    Perfil perfil;
    @Enumerated(EnumType.STRING)
    @Column(name = "up_tipo_usuario", nullable = false, length = 20)
    EnumUsuario tipoUsuario;
    @Column(name = "up_data_criacao", nullable = false)
    OffsetDateTime dataCriacao;
    @Column(name = "up_data_atualizacao", nullable = false)
    OffsetDateTime dataAtualizacao;
    @Version
    @Column(name = "up_versao", nullable = false)
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
}
