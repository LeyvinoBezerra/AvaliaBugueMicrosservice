INSERT INTO bt.perfis(per_nome, per_permissao, per_acesso_global)
VALUES ('ADMIN', 'ADMIN', TRUE),
       ('BUGUEIRO', 'BUGUEIRO', FALSE),
       ('CLIENTE', 'CLIENTE', FALSE),
       ('VISITANTE', 'VISITANTE', FALSE)
ON CONFLICT(per_nome) DO NOTHING;
