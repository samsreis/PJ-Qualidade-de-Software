# Sistema de Organização de Recursos

## Integrantes
Nome 1, Nome 2

## Pré-requisitos
Java 21 e Maven.

## Como rodar
```bash
./mvnw spring-boot:run
```
A aplicação inicia na porta 8080.

## Rodar com Docker
Requer Docker Engine e Docker Compose v2:

```bash
cp .env.example .env
docker compose up --build
```

Detalhes, configuração de porta e encerramento: [docs/docker.md](docs/docker.md). O container usa o H2 em memória e credenciais de demonstração; os dados são perdidos ao parar.

## Comandos e exemplos
O [Makefile](Makefile) oferece atalhos para iniciar, verificar e empacotar a aplicação. Exemplos HTTP para a extensão REST Client do VS Code estão em [requests/reservas.http](requests/reservas.http).

## Como rodar testes
```bash
./mvnw -B verify
```

## Usuários de teste
Todos usam a senha `123`:

- `solicitante@teste.com` (SOLICITANTE)
- `responsavel@teste.com` (RESPONSAVEL)
- `admin@teste.com` (ADMIN)

## Endpoints principais
Autenticação HTTP Basic. Os endpoints de recursos exigem ADMIN para escrita.

| Método | Rota | Perfis permitidos | Status esperados |
|---|---|---|---|
| GET | `/api/salas`, `/api/professores`, `/api/materiais` | Autenticados | 200; 401 sem autenticação |
| GET | `/api/salas/{id}`, `/api/professores/{id}`, `/api/materiais/{id}` | Autenticados | 200, 404; 401 sem autenticação |
| POST | `/api/salas`, `/api/professores`, `/api/materiais` | ADMIN | 200; 401 sem autenticação, 403 sem permissão |
| PUT | `/api/salas/{id}`, `/api/professores/{id}`, `/api/materiais/{id}` | ADMIN | 200; 401 sem autenticação, 403 sem permissão |
| DELETE | `/api/salas/{id}`, `/api/professores/{id}`, `/api/materiais/{id}` | ADMIN | 204; 401 sem autenticação, 403 sem permissão |
| POST | `/reservas` | Autenticados; material restrito: RESPONSAVEL ou ADMIN | 201; 400 intervalo inválido, 403 material restrito, 404 material inexistente, 409 conflito, 401 sem autenticação |
| GET | `/reservas` | Autenticados | 200; 401 sem autenticação |
| PUT | `/reservas/{id}` | Dono autenticado | 200; 401 sem autenticação, 403 outro dono, 404 inexistente |
| DELETE | `/reservas/{id}` | Dono autenticado | 200; 401 sem autenticação, 403 outro dono, 404 inexistente, 409 estado não removível |

Detalhes: [docs/api.md](docs/api.md). Rastreabilidade: [docs/rtm.md](docs/rtm.md).