# Mentoria 29/01/25

---

Desafio Bcredi

## Descrição com minhas Palavras

# Cenário

Irei receber um input de dados, que pode vir em diferentes formatos (arquivo csv, linha de comando, http, amqp etc), que representam uma lista de eventos de Propostas de finaciamento.

Nesse momento, trabalharemos somente com input sendo do tipo arquivo csv.

Uma proposta de financiamento consiste em três entidades:

1. Proposal
2. Warranty
3. Proponent

## Descrição das Entidades

### Proposal

Contém as informações principais da requisição do financiamento, sendo elas: 

Id - UUID

Valor - Monetário

Prazo (em meses) - Integer

### Warranty

Contém as informações de garantia de uma proposta específica. Uma proposta pode ter N garantias. As informações contidas nessa entidade são:

Id - UUID

Valor - Monetário

Estado - UF dos estados do brasil

### Proponent

Referente a quem está solicitando a proposta, uma proposta pode ter N proponentes. As informações contidas nessa entidade são:

Id - UUID

Nome - String

Idade - Integer

Salario (base anual) - Monetário

Principal - Booleano (é o proponente principal daquela proposta ou não)

## Evento

Um evento é contido de informações básicas comuns a qualquer evento mais as informações variáveis específicas àquele tipo de evento. Sendo assim, são essas as informações contidas em um evento no input de propostas:

<id_do_evento>,<tipo_do_evento>,<ação>,<timestamp>,<id_da_proposta>,<dados_variáveis>

Dado variáveis podem ser as informações de qualquer uma das entidades descritas acima de acordo com o tipo de evento.

Os tipos de evento são exatamente os nomes das entidades: proposal, warranty, proponent.

Além do tipo do evento, há também a ação feita naquele evento, são essas as ações: created, added

# Necessidade

De acordo com os valores e informações na entidade Proposal, existem algumas validações que precisam ser feitas nas demais entidades para determinar se uma proposta é de fato válida ou não.

Lembre-se, novos campos e/ou validações podem surgir futuramente, com isso o código deve estar preparado para adições sem quebrar o que já existe.

As validações que devem ser implementadas atualmente são:

- **Idade do Proponente Principal**
    - Deve haver **exatamente um** proponente principal (`principal = true`).
    - O proponente principal deve ter **pelo menos 18 anos**.
- **Comprometimento de Renda**
    - O proponente principal deve ter um **salário mensal** suficiente para cobrir **pelo menos o dobro** da parcela mensal do financiamento.
    - A parcela mensal é calculada como:
    parcela mensal=prazo em mesesvalor solicitado​
        
        parcela mensal=valor solicitadoprazo em meses\text{parcela mensal} = \frac{\text{valor solicitado}}{\text{prazo em meses}}
        
    - O salário do proponente principal deve ser **maior ou igual a duas vezes a parcela mensal**.
- **Valor Total das Garantias**
    - A soma dos valores das garantias associadas à proposta deve ser **pelo menos o dobro** do valor solicitado.
- **Quantidade de Proponentes**
    - Deve haver pelo menos **dois proponentes** na proposta.

# Critério de aceite

Dada uma lista com propostas válidas e inválidas, o programa deve imprimir no console os ids da proposta mais a informação se a mesma é valida ou não para cada uma das propostas dentro do input, que por hora será um arquivo.

## Plano de ação

**Descrição pensando no input de dado como arquivo:**

**Caso de uso 1: Parser do arquivo**

1 - Irei criar um parser do conteúdo do arquivo que transformará em uma Lista de Lista de Strings, ou seja, cada linha do arquivo será convertido para uma Lista de String.

2 - Vou printar o resultado da lista e ver se de fato separei cada linha de forma correta nas Listas individuais.

**Caso de uso 2: Mapper das entidades (depende do caso de uso 1)**

1 - Vou criar classes Java que representam as 3 entidades descritas acima em dtos que ainda não terão nenhuma validação de regra de negócio descrita acima, somente conteram os dados equivalentes de cada entidade.

2 - Vou mapear as listas de strings parseadas no caso de uso 1 para um HashMap que contem como chave o UUID da proposta e uma classe que agregue os DTOscomo valor… conforme novas entidades relacionadas a um UUID existente no map forem sendo parseadas, atualizarei a classe agregando os DTOs.

Pergunta 1: Um evento de criação de proposta sempre vem em ordem, antes dos demais eventos? Ou seja, os eventos de adição de Warranty e Proponent, podem aparecer antes do evento de criação da proposta que ele referencia? 

Pergunta 2: Esses dois eventos sempre aparecem somente após o evento da proposta que ele referencia, ou poderiam aparecer no final do arquivo por exemplo?

**Caso de uso 3: Validação das propostas**

1 - Criarei as entidades de fato representando a Proposta, Warranty e Proponent, de modo que a entidade proposta conterá uma lista de warranty e proponents. No construtor da entidade proposta implementarei as validações descritas

2 - Farei um loop pelo mapa de DTOs e transformarei em um mapa com o id da proposta e como valor sendo se a memsa é valida ou nao, validarei ANTES de chamar o construtor das entidades também as regras descritas. Estamos implementando um design DEFENSIVO, onde a entidade final sempre fará uma dupla validação de regras intrinsecas a ela mesmo

3 -

## Cenários de Teste