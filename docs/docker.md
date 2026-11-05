# Execucao com Docker

Esta configuracao e voltada para desenvolvimento e demonstracao. A aplicacao usa H2 em memoria e dados de exemplo; reservas e alteracoes desaparecem quando o container para.

## Requisitos

- Docker Engine com Docker Compose v2.

## Iniciar

Na raiz do projeto:

```bash
cp .env.example .env
docker compose up --build
```

A API fica em `http://localhost:8080`. Para escolher outra porta no host, defina `APP_PORT` antes de iniciar:

```bash
APP_PORT=8081 docker compose up --build
```

Para iniciar em segundo plano e acompanhar os logs:

```bash
docker compose up --build -d
docker compose logs -f organizacao
```

Para parar e remover o container:

```bash
docker compose down
```

## Dados de demonstracao

Os usuarios de teste usam a senha `123`:

- `solicitante@teste.com`
- `responsavel@teste.com`
- `admin@teste.com`

Essas credenciais, o console H2 e o banco em memoria sao apenas para desenvolvimento. Nao exponha esta configuracao como ambiente de producao.
