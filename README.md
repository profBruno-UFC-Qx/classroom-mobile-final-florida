# :checkered_flag: Sistema Mobile de Gestão de Orçamentos e Recibos

> Aplicativo Android para digitalização e gerenciamento de orçamentos e recibos, com geração de PDF e armazenamento local.

## :bulb: Objetivo Geral
Desenvolver um aplicativo mobile para a plataforma Android, utilizando **Kotlin**, que permita o cadastro de clientes e o gerenciamento completo de orçamentos e recibos, com exportação de documentos em formato **PDF** e armazenamento local dos dados no dispositivo do usuário — funcionando totalmente offline.

## :eyes: Público-Alvo
A aplicação é voltada para profissionais e empreendedores que precisam emitir orçamentos e recibos de forma rápida e organizada, sem depender de papéis ou planilhas:
 
- ⚡ Profissionais autônomos (eletricistas, encanadores, mecânicos, pintores, etc.)
- 🏪 Pequenos empreendedores
- 🤝 Prestadores de serviço em geral
- 📋 Qualquer pessoa que precise gerar documentos financeiros simples com agilidade

## :star2: Impacto Esperado
Com a digitalização do processo de criação de orçamentos e recibos, espera-se que o aplicativo proporcione:
 
- **Redução de erros** causados por registros manuais em papel
- **Aumento da produtividade** com a geração rápida de documentos profissionais
- **Melhor organização** do histórico de clientes e serviços prestados
- **Facilidade de compartilhamento** dos documentos via PDF, por WhatsApp, e-mail, etc.
- **Acessibilidade** para profissionais com baixo letramento digital, por meio de uma interface simples e intuitiva
- **Autonomia total** ao usuário, sem necessidade de internet ou assinaturas em serviços externos


## :triangular_flag_on_post:	 Principais funcionalidades da aplicação

| Funcionalidade          | Descrição                                                |
|-------------------------|----------------------------------------------------------|
| 📋 Cadastro de Clientes | Registrar e gerenciar informações dos clientes           |
| 💰 Orçamentos           | Criar, editar e acompanhar orçamentos                    |
| 🧾 Recibos              | Gerar recibos vinculados a orçamentos aprovados          |
| 📄 Exportação em PDF    | Exportar documentos formatados prontos para envio        |
| 📱 Armazenamento Local  | Dados salvos no dispositivo, sem necessidade de internet |
| 🔍 Listagem e Busca     | Visualizar e filtrar registros cadastrados               |
| ✏️ Edição e Exclusão    | Gerenciar e atualizar todos os dados cadastrados         | 


## :hammer_and_wrench: Tecnologias

O projeto utiliza uma stack Android moderna, com foco em funcionamento offline, persistência local e interface declarativa.

| Tecnologia             | Uso no projeto                                                                          |
|------------------------|-----------------------------------------------------------------------------------------|
| **Kotlin**             | Linguagem principal da aplicação                                                        |
| **Android SDK**        | Plataforma nativa do aplicativo                                                         |
| **Jetpack Compose**    | Construção das telas e componentes visuais                                              |
| **Material 3**         | Componentes de UI, tema, cards, botões, barras e diálogos                               |
| **Navigation Compose** | Navegação entre Home, Clientes, Orçamentos, Recibos, Configurações e Detalhe de Cliente |
| **Room**               | Banco de dados local SQLite com DAOs, entidades, relações e migrations                  |
| **KSP**                | Processamento de anotações do Room                                                      |
| **Kotlin Coroutines**  | Execução assíncrona de operações de banco e estado                                      |
| **Flow / StateFlow**   | Observação reativa de dados locais                                                      |
| **ViewModel**          | Retenção de estado e mediação entre UI e repositórios                                   |
| **PdfDocument**        | Geração nativa de PDFs de orçamento e recibo                                            |
| **FileProvider**       | Compartilhamento seguro dos PDFs com outros apps                                        |
| **Android Resources**  | Centralização de textos e suporte a internacionalização                                 |

## :building_construction: Arquitetura Atual

A arquitetura atual está em transição para uma separação mais limpa entre UI, estado e dados. O app já possui persistência local, repositórios e um ViewModel central para evitar acesso direto ao banco nas telas principais.

Fluxo geral:

```text
Compose UI
   ↓ eventos do usuário
AppNavigatorViewModel / SessionManager
   ↓ chamadas assíncronas
Repositories
   ↓
Room DAOs
   ↓
SQLite local
```

Fluxo reativo de leitura:

```text
Room Flow
   ↓
Repository
   ↓
StateFlow no ViewModel
   ↓
collectAsState() no Compose
   ↓
Tela atualizada automaticamente
```

### Camada de UI

Local principal:

```text
app/src/main/java/com/example/florida/ui
```

Responsabilidades:

- Renderizar telas com Jetpack Compose.
- Receber eventos do usuário.
- Exibir estados vindos do ViewModel.
- Navegar entre os fluxos principais.

Pastas principais:

| Pasta              | Responsabilidade                                                    |
|--------------------|---------------------------------------------------------------------|
| `ui/home`          | Home, dashboard inicial e cartão de perfil                          |
| `ui/client`        | Lista, card, criação, edição e detalhe de cliente                   |
| `ui/budget`        | Listagem e criação de orçamentos                                    |
| `ui/receipt`       | Listagem e criação de recibos                                       |
| `ui/settings`      | Configuração dos dados do emissor                                   |
| `ui/AppNavigation` | Rotas, bottom navigation, scaffold e ViewModel central de navegação |
| `ui/utils`         | Utilitários de UI/PDF, máscaras e compartilhamento                  |
| `ui/theme`         | Tema Material 3, cores e tipografia                                 |

### Camada de Estado

Hoje existem dois pontos principais de estado:

| Componente              | Responsabilidade                                                                  |
|-------------------------|-----------------------------------------------------------------------------------|
| `AppNavigatorViewModel` | Observa clientes, orçamentos e recibos; cria, edita, exclui e atualiza documentos |
| `SessionManager`        | Mantém o estado do usuário configurado no app                                     |

Observação importante:

- `AppNavigatorViewModel` já melhora a separação entre UI e dados.
- `SessionManager` ainda é um singleton global e deve futuramente ser substituído por um `SessionViewModel` ou `SettingsViewModel`.

### Camada de Domínio / Modelos

Local:

```text
app/src/main/java/com/example/florida/model
```

Modelos principais:

| Modelo         | Uso                                                                   |
|----------------|-----------------------------------------------------------------------|
| `Client`       | Representa cliente do usuário                                         |
| `Budget`       | Representa orçamento com cliente, itens, total e status               |
| `Receipt`      | Representa recibo com cliente, itens e total                          |
| `Item`         | Item de serviço usado em orçamento e recibo                           |
| `UserSetup`    | Dados do emissor usados nos PDFs                                      |
| `BudgetStatus` | Status do orçamento: rascunho, enviado, aprovado, recusado e expirado |

### Camada de Persistência

Local:

```text
app/src/main/java/com/example/florida/persistence
```

Responsabilidades:

- Definir banco Room.
- Definir entidades persistidas.
- Criar DAOs.
- Mapear entidades para modelos da aplicação.
- Executar migrations.
- Salvar imagens locais.

Pastas principais:

| Pasta/Arquivo         | Responsabilidade                                  |
|-----------------------|---------------------------------------------------|
| `AppDatabase.kt`      | Configuração principal do Room                    |
| `DatabaseProvider.kt` | Criação singleton do banco e repositórios         |
| `Entity`              | Entidades Room persistidas                        |
| `dao`                 | Consultas e comandos SQL via Room                 |
| `relations`           | Relações Room, como orçamento com itens e cliente |
| `reposity`            | Repositórios usados pela camada de estado         |
| `migration`           | Migrations versionadas do banco                   |
| `ImageStorage.kt`     | Persistência local de imagens selecionadas        |

### Banco de Dados Local

O app usa Room sobre SQLite. O banco atual possui entidades para:

- `user_setup`
- `clients`
- `budgets`
- `budget_items`
- `receipts`
- `receipt_items`

Relacionamentos principais:

```text
Client 1 ── N Budget
Budget 1 ── N BudgetItem

Client 1 ── N Receipt
Receipt 1 ── N ReceiptItem

Budget 0..1 ── 0..1 Receipt
```

Regras atuais:

- Cliente usa exclusão lógica por campo `deleted`.
- Orçamentos e recibos são removidos fisicamente quando excluídos.
- Itens de orçamento/recibo são apagados por cascade.
- Orçamento possui status persistido.
- Recibo pode ter vínculo opcional com orçamento de origem por `budgetId`.
- Cada orçamento pode gerar no máximo um recibo vinculado.
- Valores monetários são armazenados como centavos em `Long`.
- Migrations Room foram adicionadas para evitar perda de dados em atualização de schema.

### PDFs e Compartilhamento

Arquivos principais:

```text
ui/utils/BudgetPdfCreator.kt
ui/utils/ReceiptPdfCreate.kt
ui/utils/PdfShare.kt
```

Responsabilidades:

- Gerar PDF de orçamento.
- Gerar PDF de recibo.
- Compartilhar PDF via `FileProvider`.

Fluxo:

```text
Usuário toca em PDF
   ↓
Tela chama criador de PDF
   ↓
Arquivo é salvo no cache do app
   ↓
PdfShare cria Intent ACTION_SEND
   ↓
Android abre seletor de apps
```

### Navegação

Arquivos principais:

```text
ui/AppNavigation/Route.kt
ui/AppNavigation/NavigationAction.kt
ui/AppNavigation/AppNavigation.kt
```

Rotas atuais:

| Rota                  | Tela                        |
|-----------------------|-----------------------------|
| `home`                | Dashboard inicial           |
| `clients`             | Lista de clientes           |
| `clients/{clientId}`  | Detalhe de cliente          |
| `budget`              | Lista/criação de orçamentos |
| `budget/{budgetId}`   | Detalhe de orçamento        |
| `receipt`             | Lista/criação de recibos    |
| `receipt/{receiptId}` | Detalhe de recibo           |
| `settings`            | Configurações do emissor    |

### Funcionalidades Implementadas Atualmente

- Onboarding do emissor.
- Edição de dados do emissor em configurações.
- Cadastro, edição, listagem e exclusão lógica de clientes.
- Detalhe de cliente com histórico.
- Criação e listagem de orçamentos.
- Detalhe de orçamento.
- Status de orçamento.
- Aprovação/recusa de orçamento.
- Criação de recibo a partir de orçamento aprovado.
- Bloqueio de recibo duplicado para o mesmo orçamento.
- Criação e listagem de recibos.
- Detalhe de recibo.
- Geração e compartilhamento de PDF.
- Dashboard com métricas iniciais.
- Textos visíveis centralizados em `strings.xml`.
- Tradução em inglês disponível em `values-en/strings.xml`.

## :triangular_ruler: Decisões Técnicas Importantes

### Offline-first

O aplicativo foi desenhado para funcionar sem internet. Todos os dados principais ficam no banco local do dispositivo.

### Room com migrations

O projeto não deve usar migração destrutiva em produção, pois isso apagaria dados do usuário. As mudanças de schema devem ser feitas com migrations versionadas.

### PDFs nativos

Os PDFs são gerados com `PdfDocument`, sem depender de serviços externos. Isso mantém o app offline e reduz dependências.

### FileProvider

O compartilhamento de PDFs usa `FileProvider`, que é a forma segura de expor arquivos privados do app para outros aplicativos Android.

### ViewModel como fronteira da UI

A UI deve falar com ViewModels, não diretamente com DAOs ou repositórios. O projeto já iniciou essa separação com `AppNavigatorViewModel`.

## :warning: Dívidas Técnicas Conhecidas

- `SessionManager` ainda deve ser substituído por ViewModel.
- O pacote `reposity` possui erro de digitação e deve virar `repository`.
- O pacote `extencions` possui erro de digitação e deve virar `extensions`.
- Alguns textos ainda estão hardcoded nas telas e devem ir para `strings.xml`.
- Ainda faltam testes unitários, testes de DAO e testes de UI.
- Falta edição completa de orçamentos e recibos.
- Falta vínculo formal entre recibo e orçamento de origem.


continuar a conversa `codex resume 019de04c-116a-7912-8389-ba4b44634fe6`