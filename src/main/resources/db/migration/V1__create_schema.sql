CREATE SCHEMA IF NOT EXISTS bt;
CREATE TABLE bt.usuarios
(
    usu_id               BIGSERIAL PRIMARY KEY,
    usu_nome             VARCHAR(100) NOT NULL,
    usu_email            VARCHAR(150) NOT NULL,
    usu_senha            VARCHAR(255) NOT NULL,
    usu_tipo             VARCHAR(20)  NOT NULL CHECK (usu_tipo IN ('ADMIN', 'BUGUEIRO', 'CLIENTE', 'VISITANTE')),
    usu_ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
    usu_data_criacao     TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usu_data_atualizacao TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_usuarios_email UNIQUE (usu_email)
);
CREATE TABLE bt.pessoas
(
    pes_id               BIGSERIAL PRIMARY KEY,
    pes_cpf              VARCHAR(11)  NOT NULL UNIQUE,
    pes_nome_completo    VARCHAR(128) NOT NULL,
    pes_data_nascimento  DATE         NOT NULL,
    pes_data_criacao     TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pes_data_atualizacao TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pes_versao           INTEGER      NOT NULL DEFAULT 0
);
CREATE TABLE bt.perfis
(
    per_id               BIGSERIAL PRIMARY KEY,
    per_nome             VARCHAR(30) NOT NULL,
    per_permissao        VARCHAR(20) NOT NULL CHECK (per_permissao IN ('ADMIN', 'BUGUEIRO', 'CLIENTE', 'VISITANTE')),
    per_acesso_global    BOOLEAN     NOT NULL DEFAULT FALSE,
    per_data_criacao     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    per_data_atualizacao TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    per_numero_versao    INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT uk_perfil_nome UNIQUE (per_nome)
);
CREATE TABLE bt.bugueiros
(
    bug_id               BIGSERIAL PRIMARY KEY,
    bug_nome             VARCHAR(100) NOT NULL,
    bug_email            VARCHAR(150) NOT NULL UNIQUE,
    bug_telefone         VARCHAR(20),
    bug_perfil_id        BIGINT       NOT NULL REFERENCES bt.perfis (per_id),
    bug_data_criacao     TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    bug_data_atualizacao TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    bug_versao           INTEGER      NOT NULL DEFAULT 0
);
CREATE TABLE bt.enderecos_bugueiros
(
    eb_id        BIGSERIAL PRIMARY KEY,
    eb_bug_id    BIGINT       NOT NULL REFERENCES bt.bugueiros (bug_id),
    eb_cidade    VARCHAR(50)  NOT NULL,
    eb_estado    VARCHAR(2)   NOT NULL,
    eb_praia     VARCHAR(100) NOT NULL,
    eb_principal BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE TABLE bt.usuarios_perfis
(
    up_id               BIGSERIAL PRIMARY KEY,
    up_usu_id           BIGINT      NOT NULL REFERENCES bt.usuarios (usu_id),
    up_per_id           BIGINT      NOT NULL REFERENCES bt.perfis (per_id),
    up_tipo_usuario     VARCHAR(20) NOT NULL CHECK (up_tipo_usuario IN ('ADMIN', 'BUGUEIRO', 'CLIENTE', 'VISITANTE')),
    up_data_criacao     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    up_data_atualizacao TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    up_versao           INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT uk_usuario_perfil UNIQUE (up_usu_id, up_per_id)
);
CREATE TABLE bt.avaliacoes
(
    ava_id                    BIGSERIAL PRIMARY KEY,
    ava_seguranca             INTEGER     NOT NULL CHECK (ava_seguranca BETWEEN 0 AND 10),
    ava_conhecimento_roteiro  INTEGER     NOT NULL CHECK (ava_conhecimento_roteiro BETWEEN 0 AND 10),
    ava_conforto_veiculo      INTEGER     NOT NULL CHECK (ava_conforto_veiculo BETWEEN 0 AND 10),
    ava_simpatia_motorista    INTEGER     NOT NULL CHECK (ava_simpatia_motorista BETWEEN 0 AND 10),
    ava_experiencia_geral     INTEGER     NOT NULL CHECK (ava_experiencia_geral BETWEEN 0 AND 10),
    ava_adaptabilidade        INTEGER     NOT NULL CHECK (ava_adaptabilidade BETWEEN 0 AND 10),
    ava_paradas_interessantes INTEGER     NOT NULL CHECK (ava_paradas_interessantes BETWEEN 0 AND 10),
    ava_diferencial           VARCHAR(255),
    ava_feedback              VARCHAR(500),
    ava_avaliador_id          BIGINT      NOT NULL REFERENCES bt.usuarios (usu_id),
    ava_bugueiro_id           BIGINT      NOT NULL REFERENCES bt.usuarios (usu_id),
    ava_data_criacao          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ava_data_atualizacao      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_avaliacao_par UNIQUE (ava_avaliador_id, ava_bugueiro_id),
    CONSTRAINT ck_avaliacao_pares_diferentes CHECK (ava_avaliador_id <> ava_bugueiro_id)
);
CREATE INDEX ix_avaliacoes_bugueiro ON bt.avaliacoes (ava_bugueiro_id);
CREATE INDEX ix_avaliacoes_avaliador ON bt.avaliacoes (ava_avaliador_id);
CREATE INDEX ix_usuarios_tipo ON bt.usuarios (usu_tipo);
