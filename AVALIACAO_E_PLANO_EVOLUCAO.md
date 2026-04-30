# Avaliacao e Plano de Evolucao

Este documento descreve o estado atual do projeto, os riscos tecnicos encontrados e um plano de evolucao em etapas. Ele foi escrito para ser facil de seguir por uma IA ou por outro desenvolvedor.

## Contexto do Produto

O projeto e um aplicativo Android offline para profissionais e pequenos prestadores de servico criarem clientes, orcamentos, recibos e PDFs. A base atual usa Kotlin, Jetpack Compose, Room, Navigation Compose e geracao manual de PDF via `PdfDocument`.

## Estado Atual

### Evolucao aplicada apos a avaliacao

- Dashboard inicial substituido por um resumo util do negocio.
- A tela inicial agora mostra:
  - total recebido em recibos
  - total em orcamentos
  - quantidade de clientes ativos
  - valor recebido no mes atual
  - atalhos para clientes, orcamentos e recibos
  - acoes rapidas para novo orcamento e novo recibo
  - ultimos documentos criados
- `HomeScreen` passou a receber clientes, orcamentos e recibos observados pelo `AppNavigator`.
- O dashboard ainda calcula metricas dentro da UI. Isso e aceitavel nesta etapa, mas deve migrar para `HomeViewModel`/`DashboardViewModel` na Fase 1 do plano.
- Criado `AppNavigatorViewModel` para remover acesso direto aos repositorios do composable de navegacao.
- Adicionada dependency `lifecycle-viewmodel-compose` para integrar ViewModel ao Compose.
- Removido `fallbackToDestructiveMigration()` e adicionada migration Room de `1` para `2`.
- Insercao de orcamentos/recibos com itens passou a ser transacional nos DAOs.
- Adicionado `FileProvider` e utilitario de compartilhamento de PDF.
- Cards de orcamento e recibo agora possuem acao de PDF para gerar e compartilhar documentos.
- Exclusao de cliente agora abre confirmacao antes de apagar.
- Tela de configuracoes passou a permitir editar dados do emissor usados nos PDFs.

### O que ja existe

- Aplicativo Android com Compose.
- Onboarding simples para configurar o usuario emissor.
- Cadastro e listagem basica de clientes.
- Persistencia local com Room para usuario, clientes, orcamentos, recibos e itens.
- Listagem basica de orcamentos e recibos.
- Formularios basicos para criar orcamento e recibo com itens e total calculado.
- Funcoes de geracao de PDF para orcamento e recibo.

### O que ainda esta incompleto

- Nao ha arquitetura clara de presentation/domain/data.
- Nao ha ViewModels; a UI acessa repositorios diretamente.
- Nao ha estados de tela robustos: loading, erro, vazio, sucesso e validacao ainda estao misturados na UI.
- Nao ha edicao de clientes, orcamentos ou recibos.
- Nao ha tela de detalhe real para cliente, orcamento ou recibo.
- Nao ha exportacao/compartilhamento de PDF integrada ao fluxo do usuario.
- Nao ha testes relevantes.
- Nao ha validacao consistente de CPF/CNPJ, telefone, dinheiro, quantidade e campos obrigatorios.
- Nao ha estrategia de migracao de banco; `fallbackToDestructiveMigration()` apaga dados em mudancas de schema.
- Muitas strings estao hardcoded em portugues dentro das telas.
- A tela de configuracoes existe, mas esta vazia.

## Code Review Critico

### Severidade Alta

1. `DatabaseProvider.kt` usa `fallbackToDestructiveMigration()`.
   - Arquivo: `app/src/main/java/com/example/florida/persistence/DatabaseProvider.kt`
   - Problema: qualquer alteracao futura no schema pode apagar todos os dados locais do usuario.
   - Impacto: perda de clientes, orcamentos e recibos em atualizacoes.
   - Acao recomendada: remover `fallbackToDestructiveMigration()` e criar migrations versionadas do Room.

2. `SessionManager` e um singleton global com `CoroutineScope` solto.
   - Arquivo: `app/src/main/java/com/example/florida/model/SessionManager.kt`
   - Problema: o objeto cria escopos manualmente e atualiza estado Compose fora de uma camada lifecycle-aware.
   - Impacto: risco de vazamento, estado dificil de testar e erros sutis de concorrencia.
   - Acao recomendada: substituir por `UserViewModel` ou `SessionViewModel`, injetando `UserRepository`.

3. `AppNavigator` acessa repositorios diretamente e concentra regras de tela.
   - Arquivo: `app/src/main/java/com/example/florida/ui/AppNavigation/AppNavigation.kt`
   - Problema: navegacao, criacao de repositorios, coleta de flows e comandos de persistencia estao todos no mesmo composable.
   - Impacto: acoplamento alto, baixa testabilidade e crescimento dificil.
   - Acao recomendada: mover cada fluxo para seu ViewModel: `ClientViewModel`, `BudgetViewModel`, `ReceiptViewModel`.

4. Salvamento de orcamento/recibo nao e transacional.
   - Arquivos: `BudgetRepository.kt`, `ReceiptRepository.kt`
   - Problema: insere primeiro o documento e depois os itens. Se a insercao de itens falhar, o banco fica com documento sem itens.
   - Impacto: dados inconsistentes e totais incorretos.
   - Acao recomendada: criar metodos `@Transaction` nos DAOs para salvar documento e itens atomicamente.

5. PDFs nao estao integrados ao fluxo real do app.
   - Arquivos: `BudgetPdfCreator.kt`, `ReceiptPdfCreate.kt`, telas de budget/receipt.
   - Problema: a geracao existe, mas as telas nao oferecem acao de gerar, visualizar ou compartilhar PDF.
   - Impacto: uma funcionalidade central do produto ainda nao esta entregue ao usuario.
   - Acao recomendada: criar `PdfRepository` ou `DocumentShareService`, gerar arquivo com nome unico e compartilhar via `FileProvider`.

### Severidade Media

6. Formularios misturam estado local, validacao, parsing e comando de persistencia.
   - Arquivos: `BudgetScreen.kt`, `ReceiptScreen.kt`, `CreateClientDialog.kt`, `OnboardingScreen.kt`
   - Problema: regra de negocio esta dentro dos composables.
   - Impacto: telas dificeis de testar e duplicacao de logica.
   - Acao recomendada: criar classes de estado e eventos por tela, por exemplo `BudgetFormState` e `BudgetFormEvent`.

7. Campos monetarios usam `Double`.
   - Arquivos: modelos e entidades de itens/orcamentos/recibos.
   - Problema: `Double` pode gerar imprecisao em valores financeiros.
   - Impacto: arredondamentos errados em totais e PDFs.
   - Acao recomendada: salvar dinheiro como centavos em `Long` ou usar `BigDecimal` convertido para string/long no banco.

8. Strings hardcoded e inconsistentes.
   - Arquivos: `BudgetScreen.kt`, `ReceiptScreen.kt`, `OnboardingScreen.kt`, PDFs.
   - Problema: varios textos nao usam `strings.xml`.
   - Impacto: manutencao ruim e impossibilidade de localizacao consistente.
   - Acao recomendada: mover todos os textos de UI para `strings.xml`; no PDF, centralizar labels em uma classe de template.

9. Alguns componentes de cliente tem codigo morto ou comportamento quebrado.
   - Arquivo: `ClientCard.kt`
   - Problema: `showConfirm` e `DeleteConfirmDialog` existem, mas o icone de excluir chama `onDelete()` direto, sem abrir confirmacao.
   - Impacto: exclusao acidental.
   - Acao recomendada: alterar `onDeleteClick` para `showConfirm = true` e remover dialog duplicado nao usado.

10. Nomes de packages e arquivos tem erros de digitacao.
    - Exemplos: `extencions`, `reposity`, `ReceiptPdfCreate`.
    - Impacto: baixa qualidade percebida e dificuldade de navegacao.
    - Acao recomendada: renomear para `extensions`, `repository`, `ReceiptPdfCreator`.

11. Onboarding ficou longo demais para uma unica tela simples.
    - Arquivo: `OnboardingScreen.kt`
    - Problema: muitos campos em uma coluna unica.
    - Impacto: UX fraca em celulares pequenos.
    - Acao recomendada: dividir em secoes ou passos: dados pessoais, endereco, logo.

12. `SettingsScreen` e `ClientDetailScreen` estao vazias.
    - Arquivos: `SettingsScreen.kt`, `ClientDetailScreen.kt`
    - Impacto: navegacao aponta para areas sem funcionalidade.
    - Acao recomendada: implementar edicao do usuario em configuracoes e detalhe com historico do cliente.

### Severidade Baixa

13. Ha imports nao usados em algumas telas.
    - Impacto: ruido e queda de legibilidade.
    - Acao recomendada: rodar optimize imports/format.

14. Uso de APIs depreciadas.
    - Exemplos: `Locale("pt", "BR")`, `Icons.Filled.ArrowBack`, `menuAnchor()`.
    - Impacto: warnings de build.
    - Acao recomendada: atualizar para APIs recomendadas.

15. O app ainda usa namespace e applicationId genericos.
    - Arquivo: `app/build.gradle.kts`
    - Problema: `com.example.florida`.
    - Acao recomendada: definir package final do produto antes de publicar.

## Plano de Evolucao

### Fase 1 - Estabilizar a base

Objetivo: evitar perda de dados, reduzir acoplamento e preparar o app para crescer.

Tarefas:

1. Criar uma organizacao de pacotes:
   - `data/local/entity`
   - `data/local/dao`
   - `data/repository`
   - `domain/model`
   - `ui/<feature>`
   - `ui/components`
   - `ui/navigation`

2. Renomear packages com erros:
   - `extencions` para `extensions`
   - `reposity` para `repository`
   - `Entity` para `entity`

3. Adicionar ViewModels:
   - `SessionViewModel`
   - `ClientViewModel`
   - `BudgetViewModel`
   - `ReceiptViewModel`
   - `SettingsViewModel`

4. Remover acesso direto a repositorios dentro de composables.

5. Remover `fallbackToDestructiveMigration()` e criar migrations Room.

6. Criar estados padronizados:
   - `UiState.Loading`
   - `UiState.Empty`
   - `UiState.Success<T>`
   - `UiState.Error`

Critério de aceite:

- O app compila.
- Nenhum composable cria repositorio diretamente.
- Banco nao usa migracao destrutiva.
- Fluxos principais continuam funcionando: onboarding, listar/criar cliente, listar/criar orcamento, listar/criar recibo.

### Fase 2 - Completar CRUD e fluxos principais

Objetivo: entregar os fluxos que o usuario espera de um app de orcamento/recibo.

Tarefas:

1. Cliente:
   - Criar tela de detalhe do cliente.
   - Editar cliente.
   - Excluir cliente com confirmacao.
   - Mostrar historico de orcamentos e recibos do cliente.

2. Orcamento:
   - Criar detalhe do orcamento.
   - Editar dados e itens.
   - Excluir com confirmacao.
   - Gerar PDF.
   - Compartilhar PDF.
   - Criar recibo a partir de um orcamento aprovado.
   - Adicionar status: `DRAFT`, `SENT`, `APPROVED`, `REJECTED`, `EXPIRED`.

3. Recibo:
   - Criar detalhe do recibo.
   - Editar antes de compartilhar.
   - Excluir com confirmacao.
   - Gerar PDF.
   - Compartilhar PDF.
   - Vincular recibo a orcamento opcionalmente.

4. Configuracoes:
   - Editar dados do usuario emissor.
   - Editar logo.
   - Definir texto padrao de pagamento.
   - Definir validade padrao de orcamento.

Critério de aceite:

- Usuario consegue criar cliente, criar orcamento para esse cliente, gerar PDF e compartilhar.
- Usuario consegue criar recibo vinculado a cliente e compartilhar.
- Edicao e exclusao funcionam com confirmacao visual.

### Fase 3 - Qualidade de dados e dinheiro

Objetivo: evitar dados invalidos e erros financeiros.

Tarefas:

1. Trocar valores monetarios para centavos em `Long`.
2. Criar helpers:
   - `Money`
   - `CurrencyFormatter`
   - `CurrencyTextField`
3. Validar campos:
   - nome obrigatorio
   - documento opcional mas formatado quando preenchido
   - telefone opcional mas formatado quando preenchido
   - quantidade maior que zero
   - preco maior que zero
   - ao menos um item para orcamento/recibo
4. Criar mensagens de erro por campo.
5. Normalizar documentos e telefones no banco sem mascara.

Critério de aceite:

- Nao e possivel salvar documentos sem item.
- Total exibido na lista, detalhe e PDF e sempre igual.
- Valores monetarios nao dependem de `Double`.

### Fase 4 - PDF, arquivos e compartilhamento

Objetivo: transformar a geracao de PDF em uma funcionalidade confiavel.

Tarefas:

1. Criar `DocumentPdfService`.
2. Criar nomes unicos de arquivo:
   - `orcamento_<id>_<cliente>_<data>.pdf`
   - `recibo_<id>_<cliente>_<data>.pdf`
3. Usar `FileProvider` para compartilhar arquivos.
4. Salvar PDFs em diretorio privado do app ou cache conforme o caso.
5. Adicionar acao para visualizar e compartilhar.
6. Padronizar layout do PDF.
7. Garantir quebra de pagina correta para descricoes longas.
8. Criar testes manuais documentados para PDF com:
   - sem logo
   - com logo
   - muitos itens
   - descricao longa
   - cliente sem documento

Critério de aceite:

- PDF e gerado sem crash mesmo sem logo.
- PDF pode ser compartilhado por WhatsApp, email e apps de arquivo.
- PDF nao sobrescreve outro documento no cache sem controle.

### Fase 5 - Testes

Objetivo: reduzir regressao.

Tarefas:

1. Testes unitarios:
   - calculo de total
   - formatacao de moeda
   - validacao de formulario
   - mapeamento Entity <-> Domain

2. Testes de Room:
   - inserir cliente
   - soft delete de cliente
   - inserir orcamento com itens
   - inserir recibo com itens
   - consulta com relacoes

3. Testes de UI Compose:
   - onboarding
   - criar cliente
   - criar orcamento com item
   - criar recibo com item

Critério de aceite:

- `./gradlew testDebugUnitTest` passa.
- `./gradlew connectedDebugAndroidTest` passa em emulador configurado.
- Fluxos principais tem pelo menos um teste.

### Fase 6 - UX e acabamento

Objetivo: deixar o app utilizavel por profissionais com baixo letramento digital.

Tarefas:

1. Criar empty states com acoes claras.
2. Adicionar busca de clientes, orcamentos e recibos.
3. Adicionar filtros por data/status.
4. Adicionar dashboard real:
   - total de clientes
   - orcamentos do mes
   - recibos do mes
   - valor total recebido
5. Melhorar formularios:
   - campos com mascara
   - teclado numerico para telefone/dinheiro
   - validacao inline
   - botoes fixos quando formulario for longo
6. Melhorar acessibilidade:
   - content descriptions
   - contraste
   - tamanho de toque

Critério de aceite:

- Um usuario consegue entender a tela vazia sem instrucao externa.
- As acoes principais estao a no maximo dois toques da tela inicial.
- Formularios nao cortam texto em telas pequenas.

## Backlog Priorizado para a Proxima IA

Execute nesta ordem:

1. Criar ViewModels e mover os repositorios para fora dos composables.
2. Criar migrations Room e remover migracao destrutiva.
3. Implementar detalhe e edicao de cliente.
4. Corrigir confirmacao de exclusao de cliente.
5. Integrar geracao e compartilhamento de PDF para orcamento.
6. Integrar geracao e compartilhamento de PDF para recibo.
7. Trocar dinheiro de `Double` para centavos em `Long`.
8. Criar validadores de formulario e estados de erro.
9. Implementar configuracoes do usuario emissor.
10. Adicionar testes unitarios de total, validacao e mapeamento.

## Instrucoes para IA que Continuar o Projeto

1. Antes de editar, rode:
   - `find app/src/main/java -type f | sort`
   - `./gradlew :app:compileDebugKotlin`

2. Nao apague mudancas existentes do usuario.

3. Preserve o funcionamento offline.

4. Priorize Room, ViewModel, Flow e Compose state.

5. Evite criar novas dependencias sem necessidade.

6. Ao alterar banco:
   - aumente `version` em `AppDatabase`
   - crie migration
   - teste criando dados antes e depois da migracao

7. Ao alterar UI:
   - mover strings para `strings.xml`
   - verificar telas pequenas
   - evitar logica de negocio dentro de composables

8. Ao alterar PDF:
   - testar com lista vazia, lista grande e texto longo
   - nao assumir que existe imagem/logo
   - gerar arquivo com nome unico

## Definicao de Pronto Recomendada

Uma tarefa so deve ser considerada pronta quando:

- compila com `./gradlew :app:compileDebugKotlin`
- nao cria regressao no fluxo principal
- possui validacao minima de erro
- nao apaga dados locais
- nao deixa logica de persistencia dentro de composable
- documenta qualquer limitacao restante
