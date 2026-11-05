# API

Autenticação por HTTP Basic usando e-mail e senha. Os corpos usam as entidades diretamente; referências a sala, professor e material são informadas por `id`.

| Método | Rota | Perfis permitidos | Status esperados |
|---|---|---|---|
| GET | `/api/salas`, `/api/professores`, `/api/materiais` | Autenticados | 200; 401 sem autenticação |
| GET | `/api/salas/{id}`, `/api/professores/{id}`, `/api/materiais/{id}` | Autenticados | 200, 404; 401 sem autenticação |
| POST | `/api/salas`, `/api/professores`, `/api/materiais` | ADMIN | 200; 401 sem autenticação, 403 sem permissão |
| PUT | `/api/salas/{id}`, `/api/professores/{id}`, `/api/materiais/{id}` | ADMIN | 200; 401 sem autenticação, 403 sem permissão |
| DELETE | `/api/salas/{id}`, `/api/professores/{id}`, `/api/materiais/{id}` | ADMIN | 204; 401 sem autenticação, 403 sem permissão |
| POST | `/reservas` | Autenticados; material restrito: RESPONSAVEL ou ADMIN | 201; 400 intervalo inválido, 403 material restrito, 404 material inexistente, 409 conflito, 401 sem autenticação |
| GET | `/reservas` | Autenticados | 200; 401 sem autenticação |
| PUT | `/reservas/{id}` | Dono autenticado | 200; 400 intervalo inválido, 401 sem autenticação, 403 outro dono/material restrito, 404 inexistente/material inexistente, 409 conflito |
| DELETE | `/reservas/{id}` | Dono autenticado | 200; 401 sem autenticação, 403 outro dono, 404 inexistente, 409 estado não removível |

## Reservas

O POST define o solicitante pelo usuário autenticado e sempre inicia a reserva em `SOLICITADA`. O intervalo precisa ter fim posterior ao início. Conflitos de horário com reservas `SOLICITADA`, `APROVADA` ou `EM_USO` retornam 409 para sala, professor ou material informado.

Exemplo de POST:

```json
{
  "inicio": "2026-12-01T10:00:00",
  "fim": "2026-12-01T11:00:00",
  "sala": { "id": 1 },
  "professor": { "id": 1 },
  "material": { "id": 2 }
}
```

`material` é opcional. Se for restrito, apenas RESPONSAVEL ou ADMIN pode reservá-lo. PUT e DELETE exigem que o usuário autenticado seja o dono. O PUT atualiza horário e recursos, reaplica as regras de conflito e acesso a material restrito, e preserva solicitante e estado atuais. DELETE só permite estados `SOLICITADA` e `APROVADA`. Senhas nunca são incluídas nas respostas JSON.

## Recursos

Salas, professores e materiais retornam a entidade diretamente. GET exige autenticação; POST, PUT e DELETE exigem ADMIN. As coleções usam as rotas sem `{id}` e a busca/alteração/remoção usam `/{id}`.