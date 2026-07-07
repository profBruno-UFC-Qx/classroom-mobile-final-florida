# Documentação de Requisitos - Aplicativo Florida

![Homem utilizando o celular com o aplicativo Florida](imagem_requesito.jpeg)

## 1. Introdução

Este documento apresenta a documentação de requisitos do aplicativo mobile **Florida**, desenvolvido para Android. 
O objetivo do aplicativo é auxiliar profissionais autônomos, pequenos empreendedores e prestadores de serviço no cadastro de clientes, criação de orçamentos, emissão de recibos, geração de documentos em PDF e organização do histórico comercial.

## 2. Objetivo do Sistema

O sistema tem como objetivo disponibilizar uma solução mobile simples, offline e organizada para que o usuário consiga:

- Cadastrar seus dados como emissor de documentos.
- Cadastrar e gerenciar clientes.
- Criar, editar, aprovar, recusar e consultar orçamentos.
- Gerar recibos vinculados ou não a orçamentos.
- Gerar e compartilhar documentos em PDF.
- Acompanhar indicadores básicos do negócio por meio de dashboard.
- Realizar backup e restauração dos dados quando houver integração remota disponível.

## 3. Escopo

### 3.1 Dentro do Escopo

- Aplicativo Android nativo desenvolvido em Kotlin.
- Interface construída com Jetpack Compose e Material 3.
- Persistência local dos dados com Room/SQLite.
- Funcionamento offline para os principais cadastros e documentos.
- Cadastro do emissor, clientes, orçamentos e recibos.
- Geração e compartilhamento de PDFs.
- Sincronização opcional de backup por API remota.
- Validação de campos obrigatórios e formatos básicos.
- Dashboard com resumo de clientes, orçamentos, recibos e valores.

### 3.2 Fora do Escopo

- Emissão fiscal oficial de notas fiscais.
- Integração obrigatória com meios de pagamento.
- Cadastro multiusuário no mesmo aparelho.
- Controle financeiro completo com contas a pagar e receber.
- Gestão de estoque.
- Versão iOS ou web.

## 4. Elicitação de Requisitos

### 4.1 Técnicas Utilizadas

A elicitação dos requisitos foi realizada a partir das seguintes técnicas:

- **Análise documental:** leitura do README, estrutura do projeto, recursos de texto e organização dos pacotes.
- **Análise do produto implementado:** identificação das funcionalidades existentes nas telas, ViewModels, repositórios, entidades e modelos de domínio.
- **Observação dos fluxos de uso:** levantamento dos caminhos de navegação disponíveis no aplicativo, como Home, Clientes, Orçamentos, Recibos, Configurações e telas de detalhe.
- **Engenharia reversa dos requisitos:** derivação dos requisitos a partir do comportamento implementado, das validações existentes e das regras persistidas no banco.
- **Prototipação evolutiva:** consideração de que o aplicativo já funciona como protótipo/produto inicial, permitindo extrair requisitos reais a partir da versão desenvolvida.

### 4.2 Fontes de Informação

As principais fontes usadas para levantar os requisitos foram:

- Documento README do projeto.
- Telas presentes no pacote `ui`.
- Rotas de navegação definidas no aplicativo.
- Entidades Room de cliente, orçamento, recibo e emissor.
- Repositórios de persistência local.
- Serviços de geração e compartilhamento de PDF.
- Arquivos de texto da interface em `strings.xml`.
- Testes unitários de modelos e validações.

### 4.3 Partes Interessadas

| Parte interessada | Interesse no sistema |
|---|---|
| Profissional autônomo | Registrar clientes, gerar orçamentos e emitir recibos rapidamente. |
| Pequeno empreendedor | Organizar documentos comerciais sem depender de planilhas ou papel. |
| Cliente final do prestador | Receber orçamento ou recibo em PDF de forma clara e compartilhável. |
| Desenvolvedor do aplicativo | Manter uma arquitetura organizada, testável e expansível. |

### 4.4 Necessidades Identificadas

- Reduzir o uso de papel no controle de orçamentos e recibos.
- Facilitar a criação de documentos comerciais por usuários com baixa familiaridade tecnológica.
- Permitir o uso do aplicativo mesmo sem internet.
- Centralizar os dados de clientes e documentos.
- Compartilhar documentos por aplicativos já instalados no celular, como WhatsApp e e-mail.
- Proteger o histórico local contra perdas por meio de backup opcional.

## 5. Visão Geral do Produto

O Florida é um aplicativo mobile de gestão simples para prestadores de serviço. O usuário inicia configurando seus dados como emissor, incluindo identificação, telefone, endereço e imagem usada nos PDFs. Após isso, pode cadastrar clientes, criar orçamentos com itens de serviço, controlar o status dos orçamentos e gerar recibos.

O sistema possui navegação principal por abas ou barra inferior, contemplando as áreas de Home, Clientes, Orçamentos, Recibos e Opções. A tela inicial apresenta indicadores resumidos do negócio e atalhos para as principais ações.

## 6. Requisitos Funcionais

| Código | Requisito | Prioridade | Situação |
|---|---|---|---|
| RF01 | O sistema deve permitir a configuração inicial dos dados do emissor. | Alta | Implementado |
| RF02 | O sistema deve permitir editar os dados do emissor nas configurações. | Alta | Implementado |
| RF03 | O sistema deve permitir selecionar uma imagem para ser utilizada nos PDFs. | Média | Implementado |
| RF04 | O sistema deve permitir cadastrar clientes com nome, documento, telefone, endereço e imagem opcional. | Alta | Implementado |
| RF05 | O sistema deve permitir listar clientes cadastrados. | Alta | Implementado |
| RF06 | O sistema deve permitir editar dados de clientes. | Alta | Implementado |
| RF07 | O sistema deve permitir excluir clientes por exclusão lógica. | Média | Implementado |
| RF08 | O sistema deve apresentar uma tela de detalhe do cliente com histórico de documentos. | Média | Implementado |
| RF09 | O sistema deve permitir criar orçamentos associados ou não a um cliente. | Alta | Implementado |
| RF10 | O sistema deve permitir adicionar itens ao orçamento com descrição, quantidade e valor. | Alta | Implementado |
| RF11 | O sistema deve calcular o total do orçamento com base nos itens informados. | Alta | Implementado |
| RF12 | O sistema deve permitir editar orçamentos existentes. | Alta | Implementado |
| RF13 | O sistema deve permitir excluir orçamentos. | Média | Implementado |
| RF14 | O sistema deve permitir consultar detalhes de um orçamento. | Alta | Implementado |
| RF15 | O sistema deve permitir aprovar ou recusar um orçamento. | Alta | Implementado |
| RF16 | O sistema deve permitir gerar recibo a partir de orçamento aprovado. | Alta | Implementado |
| RF17 | O sistema deve impedir a criação de mais de um recibo vinculado ao mesmo orçamento. | Alta | Implementado |
| RF18 | O sistema deve permitir criar recibos independentes de orçamento. | Alta | Implementado |
| RF19 | O sistema deve permitir adicionar itens ao recibo com descrição, quantidade e valor. | Alta | Implementado |
| RF20 | O sistema deve permitir editar recibos existentes. | Alta | Implementado |
| RF21 | O sistema deve permitir excluir recibos. | Média | Implementado |
| RF22 | O sistema deve permitir consultar detalhes de um recibo. | Alta | Implementado |
| RF23 | O sistema deve gerar PDF de orçamento. | Alta | Implementado |
| RF24 | O sistema deve gerar PDF de recibo. | Alta | Implementado |
| RF25 | O sistema deve permitir compartilhar PDFs por meio de outros aplicativos Android. | Alta | Implementado |
| RF26 | O sistema deve apresentar dashboard com resumo de clientes, orçamentos, recibos e valores. | Média | Implementado |
| RF27 | O sistema deve permitir sincronizar backup remoto dos dados locais. | Média | Implementado |
| RF28 | O sistema deve permitir tentar restaurar backup durante a configuração inicial. | Média | Implementado |
| RF29 | O sistema deve permitir sair/remover os dados do emissor e voltar para a configuração inicial. | Baixa | Implementado |
| RF30 | O sistema deve exibir mensagens de erro para campos obrigatórios ou inválidos. | Alta | Implementado |

## 7. Requisitos Não Funcionais

| Código | Requisito | Prioridade | Situação |
|---|---|---|---|
| RNF01 | O aplicativo deve funcionar em dispositivos Android. | Alta | Implementado |
| RNF02 | O aplicativo deve ser desenvolvido em Kotlin. | Alta | Implementado |
| RNF03 | A interface deve utilizar Jetpack Compose. | Alta | Implementado |
| RNF04 | O sistema deve armazenar os dados principais localmente com Room/SQLite. | Alta | Implementado |
| RNF05 | O sistema deve permitir uso offline das funções principais. | Alta | Implementado |
| RNF06 | O compartilhamento de arquivos deve usar FileProvider para expor PDFs com segurança. | Alta | Implementado |
| RNF07 | As operações de dados devem ser executadas de forma assíncrona para não travar a interface. | Alta | Implementado |
| RNF08 | O sistema deve separar interface, estado, domínio, persistência e rede. | Média | Implementado |
| RNF09 | O sistema deve manter textos de interface centralizados em arquivos de recursos. | Média | Implementado |
| RNF10 | O sistema deve possuir suporte inicial a internacionalização. | Baixa | Implementado |
| RNF11 | O banco local deve possuir migrations para evolução de schema sem perda de dados. | Alta | Implementado |
| RNF12 | O sistema deve possuir testes unitários para modelos e validações principais. | Média | Implementado |

## 8. Regras de Negócio

| Código | Regra |
|---|---|
| RN01 | O emissor deve possuir nome e documento informados para concluir a configuração inicial. |
| RN02 | O documento do emissor ou cliente deve ter tamanho compatível com CPF ou CNPJ. |
| RN03 | O telefone, quando informado, deve possuir DDD e tamanho válido. |
| RN04 | O cliente deve possuir nome, documento e endereço obrigatórios. |
| RN05 | Cliente excluído deve ser marcado como removido logicamente, preservando compatibilidade com histórico e sincronização. |
| RN06 | Orçamento deve possuir ao menos um item para ser considerado válido. |
| RN07 | Item de orçamento ou recibo deve possuir descrição preenchida, quantidade maior que zero e valor maior que zero. |
| RN08 | O total de orçamento e recibo deve ser calculado a partir dos itens. |
| RN09 | Orçamento pode assumir status como rascunho, enviado, aprovado, recusado ou expirado. |
| RN10 | Apenas orçamento aprovado deve gerar recibo vinculado. |
| RN11 | Cada orçamento pode possuir no máximo um recibo vinculado. |
| RN12 | Recibo pode existir sem orçamento de origem. |
| RN13 | PDFs devem utilizar os dados do emissor configurado. |
| RN14 | PDFs devem conter informações do cliente, itens, valores e dados do documento. |
| RN15 | O backup remoto deve refletir o estado local, incluindo remoções pendentes quando aplicável. |

## 9. Casos de Uso

### UC01 - Configurar Emissor

**Ator principal:** Usuário do aplicativo.

**Fluxo principal:**

1. O usuário abre o aplicativo pela primeira vez.
2. O sistema apresenta a tela de configuração inicial.
3. O usuário informa nome, documento, telefone e endereço.
4. O usuário pode selecionar uma imagem.
5. O usuário confirma a configuração.
6. O sistema salva os dados e libera o acesso às funcionalidades principais.

**Exceções:**

- Se nome ou documento estiverem vazios, o sistema apresenta erro.
- Se documento ou telefone estiverem em formato inválido, o sistema apresenta erro.

### UC02 - Cadastrar Cliente

**Ator principal:** Usuário do aplicativo.

**Fluxo principal:**

1. O usuário acessa a área de clientes.
2. O usuário seleciona a opção de novo cliente.
3. O sistema apresenta o formulário de cadastro.
4. O usuário informa os dados do cliente.
5. O sistema valida os campos.
6. O sistema salva o cliente no banco local.
7. O cliente passa a aparecer na listagem.

### UC03 - Criar Orçamento

**Ator principal:** Usuário do aplicativo.

**Fluxo principal:**

1. O usuário acessa a área de orçamentos.
2. O usuário seleciona a opção de novo orçamento.
3. O usuário seleciona um cliente, se necessário.
4. O usuário informa observações, validade e prazo de entrega.
5. O usuário adiciona itens com descrição, quantidade e valor.
6. O sistema calcula o total.
7. O usuário salva o orçamento.
8. O sistema armazena o orçamento localmente.

### UC04 - Gerar Recibo a partir de Orçamento

**Ator principal:** Usuário do aplicativo.

**Fluxo principal:**

1. O usuário acessa o detalhe de um orçamento.
2. O usuário aprova o orçamento.
3. O sistema habilita a geração de recibo.
4. O usuário seleciona a opção de gerar recibo.
5. O sistema cria um recibo vinculado ao orçamento.
6. O recibo fica disponível para consulta e geração de PDF.

**Regra associada:** O sistema não deve permitir mais de um recibo vinculado ao mesmo orçamento.

### UC05 - Gerar e Compartilhar PDF

**Ator principal:** Usuário do aplicativo.

**Fluxo principal:**

1. O usuário acessa o detalhe de um orçamento ou recibo.
2. O usuário seleciona a opção PDF ou compartilhar.
3. O sistema gera o arquivo PDF no cache do aplicativo.
4. O sistema abre o seletor de compartilhamento do Android.
5. O usuário escolhe o aplicativo de destino.

## 10. Requisitos de Dados

### 10.1 Emissor

- Nome.
- Documento CPF/CNPJ.
- Rua.
- Número.
- Bairro.
- Cidade.
- Estado.
- Telefone.
- Caminho da imagem.
- Datas de criação e atualização.

### 10.2 Cliente

- Identificador local.
- Identificador remoto opcional.
- Nome.
- Endereço.
- Documento CPF/CNPJ.
- Telefone.
- Caminho da imagem.
- Indicador de exclusão lógica.
- Indicador de sincronização pendente.

### 10.3 Orçamento

- Identificador local.
- Identificador remoto opcional.
- Cliente vinculado opcional.
- Observações.
- Validade.
- Prazo de entrega.
- Data de criação.
- Data de atualização.
- Total.
- Status.
- Indicadores de sincronização.
- Lista de itens.

### 10.4 Recibo

- Identificador local.
- Identificador remoto opcional.
- Cliente vinculado opcional.
- Orçamento vinculado opcional.
- Total.
- Data do recibo.
- Data de criação.
- Indicadores de sincronização.
- Lista de itens.

## 11. Critérios de Aceitação

| Requisito | Critério de aceitação |
|---|---|
| Cadastro de emissor | Dado um usuário sem configuração, quando ele informar dados válidos, então o app deve salvar o emissor e abrir a área principal. |
| Cadastro de cliente | Dado um formulário de cliente válido, quando o usuário salvar, então o cliente deve aparecer na listagem. |
| Validação de cliente | Dado um cliente sem nome, documento ou endereço, quando o usuário salvar, então o sistema deve exibir erro. |
| Criação de orçamento | Dado um orçamento com itens válidos, quando o usuário salvar, então o orçamento deve ser persistido com total calculado. |
| Itens inválidos | Dado um item sem descrição, quantidade menor ou igual a zero ou valor menor ou igual a zero, quando o usuário salvar, então o sistema deve impedir a operação. |
| Aprovação de orçamento | Dado um orçamento existente, quando o usuário aprovar, então o status deve ser alterado para aprovado. |
| Recibo vinculado | Dado um orçamento aprovado sem recibo, quando o usuário gerar recibo, então deve ser criado um recibo vinculado. |
| Bloqueio de recibo duplicado | Dado um orçamento que já possui recibo vinculado, quando o usuário tentar gerar outro, então o sistema deve impedir a duplicidade. |
| Geração de PDF | Dado um orçamento ou recibo existente, quando o usuário solicitar PDF, então o sistema deve gerar um arquivo compartilhável. |
| Backup remoto | Dado que o usuário solicite sincronização, quando a API estiver disponível, então o estado local deve ser enviado ao backup remoto. |

## 12. Priorização dos Requisitos

### Alta prioridade

- Configuração do emissor.
- Cadastro e gerenciamento de clientes.
- Criação e edição de orçamentos.
- Criação e edição de recibos.
- Validação de campos.
- Geração e compartilhamento de PDF.
- Persistência local offline.

### Média prioridade

- Dashboard.
- Histórico do cliente.
- Imagem nos PDFs.
- Backup e restauração remota.
- Internacionalização.
- Migrations do banco.

### Baixa prioridade

- Logout/refazer cadastro do emissor.
- Melhorias visuais e ajustes de experiência.
- Expansão futura para integrações externas.

## 14. Restrições

- O aplicativo é voltado inicialmente para Android.
- Os dados são armazenados localmente no dispositivo.
- A sincronização remota depende da disponibilidade da API configurada.
- O PDF é gerado localmente e compartilhado por recursos nativos do Android.
- O sistema considera apenas um emissor configurado por instalação do aplicativo.

## 15. Premissas

- O usuário possui um dispositivo Android compatível.
- O usuário pode operar as principais funções sem conexão com a internet.
- O usuário precisa gerar documentos simples, não documentos fiscais oficiais.
- O cliente final aceita receber PDFs por aplicativos de mensagem ou e-mail.
- O emissor será o mesmo para todos os documentos gerados naquela instalação.
