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

| Funcionalidade | Descrição |
|---|---|
| 📋 Cadastro de Clientes | Registrar e gerenciar informações dos clientes |
| 💰 Orçamentos | Criar, editar e acompanhar orçamentos |
| 🧾 Recibos | Gerar recibos vinculados a orçamentos aprovados |
| 📄 Exportação em PDF | Exportar documentos formatados prontos para envio |
| 📱 Armazenamento Local | Dados salvos no dispositivo, sem necessidade de internet |
| 🔍 Listagem e Busca | Visualizar e filtrar registros cadastrados |
| ✏️ Edição e Exclusão | Gerenciar e atualizar todos os dados cadastrados | 


##  Tecnologias: 
Liste aqui as tecnologias e bibliotecas que foram utilizadas no projeto.

Essa é a ideia inicial do projeto. o que eu quero que vc faça é continuar esse trabalho sabendo que 

budget vai ter: 

data class BudgetEntity(// vc pode adicionar algum dado que achar enteressante
    val id: Long = 0,
    val clientId: Long?,
    val notes: String?,
    val validade: String?,
    val entrega: String?,
    val createdAt: LocalDateTime,
    val updateAt: LocalDateTime,
    val total: Double
)
receipt vai ter: 
// vc pode adicionar algum dado que achar enteressante
data class ReceiptEntity(
    val id: Long = 0,

    val clientId: Long?,
    val total: Double,
    val date: LocalDateTime,
    val createdAt: LocalDateTime
)

client vai ter: 

data class ClientEntity(// vc pode adicionar algum dado que achar enteressante
    val id: Long = 0,

    val name: String,
    val address: String,
    val document: String,
    val phone: String,
    val deleted: Boolean = false,
)

userSetup vai ter: 

data class UserSetup( // vc pode adicionar algum dado que achar enteressante
    val name: String,
    val document: String,
    val address: String
    val phone: String,
    val imagePath: String? = null,
)

e sabendo que tanto o orçamento quanto o recibo pode ter uma lista de itens quero que vc faça a continuação da implementação nesse projeto utilizando as boas praticas de probramaçaõ mobile.

as funções de criar os pdfs ja estão prontas. 