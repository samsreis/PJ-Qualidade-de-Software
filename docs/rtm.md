# Matriz de Rastreabilidade

| Requisito crítico | Teste que cobre |
|---|---|
| Autenticação (401 sem credenciais) | `ReservaApiTest.postSemAuthDeveRetornar401` |
| Autorização de perfil (403) | `ReservaApiTest.postComSolicitanteEMaterialRestritoDeveRetornar403` |
| Sobreposição de sala | `ReservaServiceTest.sobreposicaoDeSalaDeveLancarConflito` |
| Dupla reserva concorrente | `ConcorrenciaTest.apenasUmaReservaConcorrenteDeveSerCriada` |
| Material restrito | `ReservaApiTest.postComSolicitanteEMaterialRestritoDeveRetornar403` |
| Estados da reserva | Sem teste |