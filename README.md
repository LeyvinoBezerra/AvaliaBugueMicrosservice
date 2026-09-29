# BuggyTrip

# Plataforma de Avaliação de Serviços e Produtos

As plataformas de avaliação de serviços e produtos estão no centro da transformação digital. Hoje, o mundo da tecnologia vive uma fase em que a opinião do usuário é o principal motor de confiança.

---

## 🚀 Tendências atuais

- **Inteligência Artificial e Machine Learning**: sistemas que analisam automaticamente avaliações para detectar padrões, sentimentos e até fraudes.
- **Experiência do Usuário (UX)**: interfaces mais intuitivas, com dashboards que mostram métricas de satisfação em tempo real.
- **Integração omnichannel**: avaliações vindas de redes sociais, apps e sites são consolidadas em um só painel.
- **Blockchain**: começa a ser usado para garantir autenticidade das avaliações, evitando manipulações.

---

## 💡 Impacto no mercado

Empresas estão cada vez mais dependentes dessas plataformas para entender o comportamento do consumidor e ajustar seus produtos. Avaliações viraram dados estratégicos, não apenas opiniões.

API REST para cadastro de usuários e avaliação estruturada de experiências com bugueiros.

## Stack

Java 25 LTS · Spring Boot 4.1.1 · Spring MVC · JPA/Hibernate · PostgreSQL 17 · Flyway · Spring Security/JWT · SpringDoc/OpenAPI · Actuator · Docker/Compose · JUnit 5 · Testcontainers · JaCoCo · **sem Lombok**.

O desenvolvimento local segue a baseline `BuggyTrip_Documentacao_Visao_Projeto_Software_Docker_Windows.docx`. A solução não usa Terraform, Redis/cache ou microserviços.

## Executar

No PowerShell, na raiz:

```powershell
Copy-Item .env.example .env
docker compose up -d --build
docker compose ps
```

URLs:

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics

Parar:

```powershell
docker compose down
```

Recriar banco:

```powershell
docker compose down -v
docker compose up -d --build
```

## Credenciais locais

O backend cria os usuários de demonstração na primeira subida:

| Papel | E-mail | Senha |
|---|---|---|
| ADMIN | admin@buggytrip.local | Admin@123 |
| CLIENTE | cliente@buggytrip.local | Cliente@123 |
| BUGUEIRO | bugueiro@buggytrip.local | Bugueiro@123 |

Use essas credenciais somente no ambiente local.

## Fluxo ponta a ponta

### 1. Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{"email":"cliente@buggytrip.local","senha":"Cliente@123"}
```

Copie o campo `token` e, no Swagger, clique em **Authorize** e informe `Bearer <token>`.

### 2. Criar avaliação

```http
POST /api/v1/avaliacoes
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "seguranca": 10,
  "conhecimentoRoteiro": 9,
  "confortoVeiculo": 9,
  "simpatiaMotorista": 10,
  "experienciaGeral": 10,
  "adaptabilidade": 8,
  "paradasInteressantes": 9,
  "diferencial": "Conhecimento das praias",
  "feedback": "Excelente experiência.",
  "avaliadorId": 2,
  "bugueiroId": 3
}
```

A média dos sete critérios é calculada pela aplicação. O banco também impede avaliação duplicada do mesmo par avaliador/bugueiro.

## API

| Método | Endpoint | Acesso |
|---|---|---|
| POST | `/api/v1/auth/login` | Público |
| POST | `/api/v1/users` | Público |
| GET | `/api/v1/users/{id}` | JWT: próprio/ADMIN |
| GET | `/api/v1/users` | ADMIN |
| PUT | `/api/v1/users/{id}` | JWT: próprio/ADMIN |
| DELETE | `/api/v1/users/{id}` | JWT: próprio/ADMIN |
| GET | `/api/v1/avaliacoes` | JWT |
| GET | `/api/v1/avaliacoes/{id}` | JWT |
| POST | `/api/v1/avaliacoes` | JWT |
| PUT | `/api/v1/avaliacoes/{id}` | JWT |
| DELETE | `/api/v1/avaliacoes/{id}` | JWT |

Listagens suportam `page`, `size` e `sort` (por exemplo, `sort=nome,asc` ou `sort=id,desc`); avaliações aceitam `bugueiroId`. Em usuários, campos de ordenação inválidos são substituídos pela ordenação padrão por `id`, evitando erro interno.

## Banco

Hibernate está em `ddl-auto=validate`: o schema é responsabilidade exclusiva do Flyway.

```text
src/main/resources/db/migration/
├── V1__create_schema.sql
└── V2__seed_perfis.sql
```

O modelo preserva as entidades principais do projeto original (`Usuario`, `Avaliacao`, `Pessoa`, `Perfil`, `Bugueiro`, `EnderecoBugueiro`, `UsuarioPerfil`) e fortalece o núcleo de autenticação e avaliação.

## Segurança

- BCrypt para senhas.
- JWT stateless.
- Secret por variável de ambiente.
- Sessão HTTP stateless.
- Rotas de login, Swagger, OpenAPI e health públicas.
- Operações administrativas protegidas por role.
- Regras de negócio: avaliador deve ser `CLIENTE`; avaliado deve ser `BUGUEIRO`; usuários devem ser diferentes; um cliente não avalia o mesmo bugueiro duas vezes.

## Problem Details

Erros usam `ProblemDetail` e incluem `code` e `instance`, por exemplo:

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "O avaliador já avaliou este bugueiro",
  "code": "CONFLICT",
  "instance": "/api/v1/avaliacoes"
}
```

## Testes e cobertura

```powershell
.\mvnw.cmd clean verify
```

Os testes incluem:

- serviços e regras de negócio com Mockito;
- controller MVC;
- contexto completo com PostgreSQL real via Testcontainers;
- health check;
- proteção de endpoint;
- publicação OpenAPI;
- JaCoCo com gate de 70% de linhas.

Relatório: `target/site/jacoco/index.html`.

Os testes de integração exigem Docker Desktop ativo.

## Execução local sem container da aplicação

```powershell
docker compose up -d postgres
.\mvnw.cmd spring-boot:run
```

O JDK precisa ser Java 25.

## Troubleshooting

### Ver logs

```powershell
docker compose logs -f app
docker compose logs -f postgres
```

As requisições são registradas com método, rota, status, duração e um `X-Correlation-ID`, também devolvido na resposta. Logs da aplicação ficam no volume Docker `buggytrip-logs`, em `/app/logs/buggytrip.log`, e são rotacionados por tamanho (10 MB), com até 14 arquivos e limite total de 200 MB. Ajuste `LOG_FILE`, `LOG_MAX_FILE_SIZE`, `LOG_MAX_HISTORY` e `LOG_TOTAL_SIZE_CAP` no `.env` para personalizar o armazenamento. O endpoint de métricas do Actuator continua disponível para acompanhar recursos da aplicação.

### Porta 5432 ocupada

```powershell
netstat -ano | findstr :5432
```

### Recriar ambiente

```powershell
docker compose down -v
docker compose up -d --build
```

### Java incorreto

```powershell
java -version
.\mvnw.cmd -version
```

O POM usa `<release>25</release>`.

## Estrutura

```text
buggytrip/
├── src/main/java/.../
│   ├── api/controllers
│   ├── api/dtos
│   ├── api/exceptions
│   ├── domain/entities
│   ├── domain/repositories
│   ├── domain/service
│   └── infrastructure/{config,security}
├── src/main/resources/{application.yml,db/migration}
├── src/test/java
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── pom.xml
└── README.md
```

## Referências

- Spring Boot: https://docs.spring.io/spring-boot/
- Spring Security: https://docs.spring.io/spring-security/reference/
- SpringDoc: https://springdoc.org/
- Flyway: https://documentation.red-gate.com/flyway
- PostgreSQL: https://www.postgresql.org/docs/
- Testcontainers: https://testcontainers.com/
