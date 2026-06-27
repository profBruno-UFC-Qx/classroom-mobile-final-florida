# :checkered_flag: Sistema Mobile de Gestão de Orçamentos e Recibos

> Aplicativo Android para digitalização e gerenciamento de orçamentos e recibos, com geração de PDF e armazenamento local.

## :bulb: Objetivo Geral
Desenvolver um aplicativo mobile para a plataforma Android, utilizando **Kotlin**, que permita o cadastro de clientes e o gerenciamento completo de orçamentos e recibos, com exportação de documentos em formato **PDF**, armazenamento local e sincronização opcional de backup pela internet.

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

## :file_folder: Organização dos Diretórios

```text
.
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/florida/
│   │   │   │   ├── di/           # Injeção de dependências com Hilt
│   │   │   │   ├── document/     # Criação e compartilhamento de PDFs
│   │   │   │   ├── domain/       # Modelos e validações da aplicação
│   │   │   │   ├── extensions/   # Funções auxiliares de formatação
│   │   │   │   ├── network/      # API, DTOs e mapeamento dos dados remotos
│   │   │   │   ├── persistence/  # Banco Room, DAOs, entidades e repositórios
│   │   │   │   └── ui/           # Telas, componentes, navegação e ViewModels
│   │   │   ├── res/               # Textos, cores, temas, ícones e configurações
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                   # Testes unitários
│   │   └── androidTest/            # Testes instrumentados do Android
│   └── build.gradle.kts            # Configuração e dependências do módulo Android
├── gradle/                          # Catálogo de versões e Gradle Wrapper
├── build.gradle.kts                 # Configuração geral do projeto
└── settings.gradle.kts              # Módulos e repositórios do Gradle
```

Dentro de `ui`, cada funcionalidade possui sua própria pasta:

| Diretório       | Responsabilidade                                      |
|-----------------|-------------------------------------------------------|
| `ui/home`       | Tela inicial, dashboard, splash e estados de erro     |
| `ui/client`     | Cadastro, listagem, edição e detalhes de clientes     |
| `ui/budget`     | Cadastro, listagem, edição e detalhes de orçamentos   |
| `ui/receipt`    | Cadastro, listagem, edição e detalhes de recibos      |
| `ui/settings`   | Configurações e dados do emissor                      |
| `ui/onboarding` | Configuração inicial do usuário                       |
| `ui/navigation` | Rotas, barras, diálogos e navegação entre telas       |
| `ui/theme`      | Cores, tipografia e temas claro/escuro                |
| `ui/utils`      | Máscaras e utilitários usados pela interface          |

## :building_construction: Arquitetura Atual

O aplicativo separa interface, estado e acesso a dados. As telas não acessam o banco diretamente: elas enviam ações aos ViewModels, que utilizam os repositórios.

Fluxo geral:

```text
Compose UI
   ↓ eventos do usuário
ViewModels
   ↓ chamadas assíncronas
Repositories
   ↓                 ↓
Room DAOs       API remota
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
| `ui/navigation`    | Rotas, bottom navigation, scaffold e navegação entre telas          |
| `ui/utils`         | Utilitários de UI/PDF, máscaras e compartilhamento                  |
| `ui/theme`         | Tema Material 3, cores e tipografia                                 |

### Camada de Estado

O estado das telas é organizado em ViewModels:

| Componente              | Responsabilidade                                             |
|-------------------------|--------------------------------------------------------------|
| `AppNavigatorViewModel` | Estado geral, dados do emissor e sincronização do backup     |
| `SessionViewModel`      | Sessão, onboarding e restauração dos dados                   |
| `DashboardViewModel`    | Indicadores exibidos na tela inicial                         |
| `ClientViewModel`       | Estado e operações de clientes                               |
| `BudgetViewModel`       | Estado e operações de orçamentos                             |
| `ReceiptViewModel`      | Estado e operações de recibos                                |

### Camada de Domínio / Modelos

Local:

```text
app/src/main/java/com/example/florida/domain/model
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
| `AppDatabase.kt`         | Configuração principal do Room                    |
| `ImageStorageService.kt` | Persistência local de imagens selecionadas        |
| `entity`                 | Entidades Room persistidas                        |
| `dao`                    | Consultas e comandos SQL via Room                 |
| `relations`              | Relações Room, como orçamento com itens e cliente |
| `repository`             | Repositórios usados pelos ViewModels              |
| `mapper`                 | Conversão entre entidades e modelos de domínio    |
| `migration`              | Migrations versionadas do banco                   |

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
document/pdf/BudgetPdfCreator.kt
document/pdf/ReceiptPdfCreate.kt
document/pdf/PdfShare.kt
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
ui/navigation/Route.kt
ui/navigation/NavigationAction.kt
ui/navigation/AppNavHost.kt
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
- Edição de orçamentos e recibos.
- Geração e compartilhamento de PDF.
- Seleção de imagens da galeria.
- Backup e restauração por API remota.
- Dashboard com métricas iniciais.
- Testes unitários de modelos e validações.
- Textos visíveis centralizados em `strings.xml`.
- Tradução em inglês disponível em `values-en/strings.xml`.

## :triangular_ruler: Decisões Técnicas Importantes

### Offline-first

Todos os dados principais ficam no banco local do dispositivo, permitindo o uso sem internet. Quando há conexão, o aplicativo pode sincronizar um backup pela API remota.

### Room com migrations

O projeto não deve usar migração destrutiva em produção, pois isso apagaria dados do usuário. As mudanças de schema devem ser feitas com migrations versionadas.

### PDFs nativos

Os PDFs são gerados com `PdfDocument`, sem depender de serviços externos. Isso mantém o app offline e reduz dependências.

### FileProvider

O compartilhamento de PDFs usa `FileProvider`, que é a forma segura de expor arquivos privados do app para outros aplicativos Android.

### ViewModel como fronteira da UI

A UI se comunica com ViewModels, sem acessar diretamente DAOs ou repositórios. Isso mantém as regras de negócio e o acesso aos dados separados das telas.
