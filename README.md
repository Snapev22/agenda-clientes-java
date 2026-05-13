# Agenda CRUD em Java

Sistema que simula uma agenda de clientes, desenvolvido com o objetivo de praticar conceitos fundamentais do desenvolvimento backend com Java.

O projeto foi utilizado para treinar organização em camadas, persistência de dados, refatoração de código, tratamento de exceções e boas práticas de desenvolvimento.

## Objetivos do Projeto

- Praticar persistência de dados utilizando JDBC
- Aplicar arquitetura em camadas
- Exercitar modelagem de entidades
- Trabalhar tratamento de exceções e validações
- Desenvolver organização, legibilidade e manutenção de código
- Utilizar recursos modernos da linguagem Java
  
## Tecnologias e conceitos utilizados

- Java
- JDBC
- MySQL
- HikariCP
- Lombok
- Arquitetura em camadas (`main`, `service`, `dao`, `entities`)
- Padrão DAO
- Tratamento de exceções customizadas
- Builder Pattern
- Optional
- Collections imutáveis
- Paginação de resultados

## Funcionalidades

- Cadastro de pessoas
- Alteração de cadastro
- Remoção de cadastro
- Busca por ID
- Filtro por idade
- Ordenação alfabética
- Paginação de resultados via `JOptionPane`

## Como executar

1. Configure o banco de dados MySQL.
2. Crie a tabela `pessoas`.
3. Ajuste as credenciais de conexão em `ConexaoDb.java`.
4. Execute a classe `AgendaApplication`.

```sql
CREATE TABLE pessoas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    endereco VARCHAR(255),
    telefone VARCHAR(20),
    idade INT
);
