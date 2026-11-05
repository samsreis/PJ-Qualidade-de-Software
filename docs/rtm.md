# Matriz de Rastreabilidade

| Requisito crítico | Teste que cobre |
|---|---|
| Autenticação (401 sem credenciais) | `ReservaApiTest.postSemAuthDeveRetornar401` |
| Autorização de perfil (403) | `ReservaApiTest.postComSolicitanteEMaterialRestritoDeveRetornar403` |
| Sobreposição de sala | `ReservaServiceTest.sobreposicaoDeSalaDeveLancarConflito` |
| Dupla reserva concorrente | `ConcorrenciaTest.apenasUmaReservaConcorrenteDeveSerCriada` |
| Material restrito | `ReservaApiTest.postComSolicitanteEMaterialRestritoDeveRetornar403` |
| Conflito ao atualizar reserva | `ReservaApiTest.putComReservaSobrepostaDeveRetornar409` |
| Hash de senha não exposto | `ReservaApiTest.postValidoDeveRetornar201` |
| Estado e solicitante preservados na atualização | `ReservaApiTest.putValidoPreservaEstadoEPermiteManterMesmoHorario` |
| Estoque máximo por intervalo | `ReservaApiTest.postRespeitaQuantidadeDisponivelDeMaterial` |
| Pico de uso em intervalos parciais | `ReservaServiceTest.capacidadeDeMaterialConsideraPicoDeUsoNoIntervalo` |
| Quantidade positiva | `ReservaApiTest.postComQuantidadeMaterialNaoPositivaDeveRetornar400` |
| Estoque respeitado ao atualizar | `ReservaApiTest.putRecalculaEstoqueComQuantidadeAtualizada` |
| Estoque respeitado sob concorrência | `ConcorrenciaTest.estoqueDeUmaUnidadeNaoPodeSerExcedidoPorRequisicoesConcorrentes` |
| Soma sem overflow | `ReservaServiceTest.somaDeQuantidadeNaoEstouraLimiteDeInteiro` |