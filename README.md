# CRM de Clientes — Java

Sistema de gerenciamento de clientes desenvolvido em Java, simulando funcionalidades reais de um CRM para prestadores de serviços autônomos. Permite gerenciar contatos pessoa física e jurídica, acompanhar o relacionamento com cada cliente e analisar o perfil da carteira.

Projeto desenvolvido com foco em boas práticas de arquitetura backend, refatoração progressiva e aplicação de conceitos avançados de Orientação a Objetos.

---

## Funcionalidades

- Cadastro de clientes PF e PJ com validações específicas por tipo
- Busca por nome (filtro parcial), por ID e por faixa etária
- Listagem paginada e ordenação alfabética
- Alteração e remoção de cadastros com confirmação
- **Score de relacionamento**: calcula automaticamente a "saúde" do relacionamento com cada cliente com base no tempo desde o último contato e no tipo (PF/PJ), usando vetores de pesos e decaimento por faixa
- **Ranking de relacionamento**: lista clientes ordenados do mais negligenciado ao mais recente, classificados como Ótimo / Regular / Atenção / Crítico
- **Relatório da carteira**: estatísticas agregadas com distribuição por faixa etária, média de idade e taxa de completude dos cadastros

---

## Tecnologias

- Java 17+
- JDBC + MySQL
- Lombok

---

## Arquitetura

src/
├── main/         → ponto de entrada e montagem das dependências
├── controller/   → orquestração dos fluxos (AgendaController)
├── view/         → coleta de input e exibição via JOptionPane (AgendaView)
├── service/      → regras de negócio (ContatoService, ScoreRelacionamentoService, RelatorioAgendaService)
├── dao/          → acesso ao banco via JDBC (ContatoDAO)
├── entities/     → modelo de domínio (Contato, PessoaFisica, PessoaJuridica)
├── interfaces/   → contratos (Validavel)
└── exceptions/   → exceções customizadas (RegraDeNegocioException)


Injeção de dependência manual via construtor — sem frameworks. O `main` monta o grafo de dependências e delega tudo ao controller.

---

## Conceitos aplicados

**OOP**
- Herança e classe abstrata: `Contato` abstrata com `PessoaFisica` e `PessoaJuridica`
- Interface: `Validavel` implementada por cada entidade
- Encapsulamento: campos privados, getters/setters, `id` sem setter público

**Java**
- `Optional` para buscas que podem não retornar resultado
- `Stream API` no ranking de relacionamento (map, sorted, forEach)
- `Period.between` para cálculo de idade a partir da data de nascimento
- Coleções imutáveis nos retornos do DAO
- Tratamento de exceções customizadas com `throws` e `try/catch`
- Builder Pattern via Lombok `@SuperBuilder`
- `@RequiredArgsConstructor` para injeção via construtor

**Arquitetura**
- Padrão MVC adaptado para aplicação console
- Padrão DAO com single table inheritance no banco
- SRP aplicado: cada classe tem uma única razão para mudar
- Validação delegada à entidade via interface, sem acoplamento no Service

---

## Banco de dados

```sql
CREATE TABLE contatos (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    nome             VARCHAR(255) NOT NULL,
    endereco         VARCHAR(255),
    telefone         VARCHAR(20),
    tipo             VARCHAR(2)   NOT NULL DEFAULT 'PF',
    cpf              VARCHAR(14),
    data_nascimento  DATE,
    cnpj             VARCHAR(18),
    razao_social     VARCHAR(150),
    ultimo_contato   DATE
);
```

Estratégia **single table**: PF e PJ persistidos na mesma tabela com coluna `tipo`. Campos específicos de cada subtipo ficam nulos para o outro tipo.

---

## Como executar

1. Crie o banco e execute o script acima
2. Ajuste as credenciais em `ConexaoDb.java`
3. Execute `AgendaApplication.java`
