# Arquitetura

## Camadas

- `controller`: recebe requisições HTTP, aplica autenticação/autorização configurada em `security` e delega operações.
- `service`: contém regras de negócio de reservas, como validação do intervalo, conflitos e disponibilidade de materiais.
- `repository`: fornece persistência JPA para as entidades.
- `domain`: representa salas, professores, materiais, usuários e reservas.
- `DataInitializer`: carrega usuários e recursos de demonstração ao iniciar a aplicação.

## Reserva de recursos

A reserva é persistida com estado inicial `SOLICITADA`. Sala e professor não podem ter outra reserva ativa que intersecte o intervalo solicitado. Para materiais, o serviço calcula o maior consumo simultâneo durante o intervalo e o compara com o estoque; a linha do material é bloqueada durante a operação para evitar excedentes concorrentes.

Os estados `SOLICITADA`, `APROVADA` e `EM_USO` consomem disponibilidade. `CONCLUIDA`, `REJEITADA` e `CANCELADA` não consomem estoque.

## Ambiente local

A configuração padrão usa H2 em memória, autenticação HTTP Basic e contas criadas por `DataInitializer`. Reiniciar a aplicação apaga os dados. Essas configurações são destinadas a desenvolvimento e demonstração, não a produção.

## Verificações

- `./mvnw -B verify`: executa testes e empacota a aplicação.
- `make verify`: atalho equivalente, se GNU Make estiver instalado.
- `.github/workflows/verify.yml`: workflow manual no GitHub Actions para executar a verificação com Java 21.
