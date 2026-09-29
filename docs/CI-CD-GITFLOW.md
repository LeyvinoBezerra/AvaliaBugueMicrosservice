# Gitflow, CI e deploy na AWS

## Fluxo de branches

- `feature/tarefa-nomeada` abre automaticamente um PR para `develop`.
- O merge em `develop` abre automaticamente um PR de promoção para `main`.
- `bugfix/nome`, `hotfix/nome` e `rollback/nome` abrem PR para `main`.
- Depois do merge de bugfix/hotfix/rollback em `main`, é aberto um PR de sincronização de `main` para `develop`.
- PRs fora desses fluxos são reprovados pelo check `CI / validate-and-test`.
- `develop` e `main` exigem PR, ao menos uma aprovação e o check CI. Commits diretos, force-push e exclusão das branches são bloqueados.

Os slugs devem usar letras minúsculas, números e hífens. Exemplo: `feature/corrigir-cadastro-usuario`.

## Aplicar proteção das branches

Com GitHub CLI autenticado como administrador do repositório, execute na raiz do projeto:

```powershell
gh auth login
.\.github\scripts\protect-branches.ps1
```

O script configura a proteção de `develop` e `main`. Confirme também que **Settings > Actions > General > Workflow permissions** permite as permissões definidas pelos workflows. A criação automática de PRs usa `pull-requests: write`.

## Deploy ECS

O workflow executa `./mvnw verify` em PRs e pushes. Depois de um push validado em `develop`, publica no ambiente `development`; depois de um push em `main`, publica no ambiente `production`. A imagem é armazenada no ECR com a tag do SHA do commit e o serviço ECS é atualizado usando a definição de tarefa já existente.

Crie os ambientes `development` e `production` em **Settings > Environments**. Configure as variáveis abaixo em ambos (os valores podem diferir por ambiente):

| Variável | Exemplo/conteúdo |
| --- | --- |
| `AWS_ROLE_ARN` | ARN de role IAM assumida pelo GitHub via OIDC |
| `AWS_REGION` | Região AWS, por exemplo `us-east-1` |
| `ECR_REPOSITORY` | Nome do repositório ECR |
| `ECS_CLUSTER` | Nome/ARN do cluster ECS |
| `ECS_SERVICE` | Nome/ARN do serviço ECS |
| `ECS_TASK_DEFINITION` | Família ou ARN da task definition |
| `ECS_CONTAINER_NAME` | Nome do container de aplicação na task definition |

Configure o provedor OIDC `token.actions.githubusercontent.com` na conta AWS, permitindo `sts:AssumeRoleWithWebIdentity` apenas para este repositório e ambiente. O `sub` recomendado é:

```text
repo:LeyvinoBezerra/AvaliaBugueMicrosservice:environment:development
repo:LeyvinoBezerra/AvaliaBugueMicrosservice:environment:production
```

Use uma role por ambiente e conceda somente as ações necessárias: login/upload de imagens ao ECR (`ecr:GetAuthorizationToken`, `ecr:BatchCheckLayerAvailability`, `ecr:CompleteLayerUpload`, `ecr:InitiateLayerUpload`, `ecr:PutImage`, `ecr:UploadLayerPart`), leitura e atualização do serviço/task ECS (`ecs:DescribeTaskDefinition`, `ecs:DescribeServices`, `ecs:RegisterTaskDefinition`, `ecs:UpdateService`) e `iam:PassRole` restrito às roles de execução e tarefa da aplicação. Restrinja recursos às ARNs do ECR, ECS e IAM desse ambiente sempre que a AWS permitir.

Configure revisores obrigatórios no ambiente `production` para exigir aprovação antes do deploy de produção. Não armazene chaves AWS de longa duração nos secrets do GitHub; o deploy usa OIDC.

## Observações

- A infraestrutura de rede, cluster/serviço ECS, ECR, task definition, banco e roles IAM deve existir antes do primeiro deploy; o workflow não cria infraestrutura.
- A task definition ECS deve expor a porta `8080` e conter o container com o nome configurado em `ECS_CONTAINER_NAME`.
- Os testes de integração usam Testcontainers e rodam no runner Ubuntu do GitHub Actions.
- O contexto Docker exclui `.env`, logs, artefatos `target` e arquivos do Git.
