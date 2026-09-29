package br.edu.ufersa.pw.todo.buggytrip.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "enderecos_bugueiros", schema = "bt")
public class EnderecoBugueiro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eb_id")
    Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eb_bug_id", nullable = false)
    Bugueiro bugueiro;
    @Column(name = "eb_cidade", nullable = false, length = 50)
    String cidade;
    @Column(name = "eb_estado", nullable = false, length = 2)
    String estado;
    @Column(name = "eb_praia", nullable = false, length = 100)
    String praia;
    @Column(name = "eb_principal", nullable = false)
    Boolean enderecoPrincipal = false;

    public Long getId() {
        return id;
    }
}
