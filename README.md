# eFinanceira API

API REST desenvolvida em Java 21 com Spring Boot 3 para consultas otimizadas ao banco de dados do sistema eFinanceira.

## 📋 Descrição

Esta API foi criada para expor como endpoints REST todas as consultas ao banco de dados que eram realizadas diretamente pelo aplicativo ASSINADOREFINANCEIRA. O objetivo é centralizar e otimizar essas consultas, tornando-as mais performáticas, manuteníveis e acessíveis através de uma interface REST padronizada.

### Funcionalidades

A API disponibiliza os seguintes endpoints:

#### 1. Consultas de Pessoas e Contas
- **Buscar Pessoas com Contas**: Retorna pessoas físicas com suas contas e movimentações no período informado
- **Calcular Totais de Movimentação**: Calcula totais de créditos e débitos para uma conta específica no período

#### 2. Consultas de Lotes e Eventos
- **Buscar Lotes**: Lista lotes com filtros opcionais (data, período, ambiente)
- **Buscar Lote por Protocolo**: Retorna informações de um lote específico pelo protocolo
- **Buscar Eventos do Lote**: Lista todos os eventos de um lote específico
- **Verificar Abertura Enviada**: Verifica se existe uma abertura enviada e aceita para um período

## 🚀 Tecnologias

- **Java 21**: Linguagem de programação
- **Spring Boot 3.2.0**: Framework principal
- **Spring Data JPA**: Persistência de dados
- **PostgreSQL**: Banco de dados
- **Spring AOP**: Programação orientada a aspectos para logging
- **Lombok**: Redução de boilerplate
- **Maven**: Gerenciamento de dependências

## 📦 Estrutura do Projeto

```
efinanceira-api/
├── src/
│   ├── main/
│   │   ├── java/br/com/bscash/efinanceira/
│   │   │   ├── application/          # Camada de aplicação
│   │   │   │   ├── aspect/           # Aspectos AOP (logging)
│   │   │   │   ├── controller/       # Controllers REST
│   │   │   │   └── exception/        # Tratamento de exceções
│   │   │   ├── domain/                # Camada de domínio
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── model/            # Modelos de domínio
│   │   │   │   └── service/          # Serviços de negócio
│   │   │   └── infrastructure/       # Camada de infraestrutura
│   │   │       └── repository/       # Repositórios de dados
│   │   └── resources/
│   │       └── application.yml        # Configurações
│   └── test/                          # Testes
└── pom.xml                             # Configuração Maven
```

## 🏗️ Arquitetura

A API segue os princípios de **Clean Architecture** e **SOLID**:

- **Separação de Responsabilidades**: Cada camada tem uma responsabilidade específica
- **Dependency Inversion**: Dependências apontam para abstrações
- **Single Responsibility**: Cada classe tem uma única responsabilidade
- **Open/Closed**: Aberto para extensão, fechado para modificação

### Camadas

1. **Application**: Controllers, aspectos e tratamento de exceções
2. **Domain**: Lógica de negócio, modelos e DTOs
3. **Infrastructure**: Acesso a dados (repositórios)

## 🔧 Configuração

### Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- PostgreSQL (banco de dados configurado)

### Configuração do Banco de Dados

Edite o arquivo `src/main/resources/application.yml` com as credenciais do seu banco:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://HOST:PORT/DATABASE
    username: SEU_USUARIO
    password: SUA_SENHA
```

### Executando a Aplicação

```bash
# Compilar o projeto
mvn clean install

# Executar a aplicação
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 📚 Endpoints da API

### Base URL
```
http://localhost:8080/api/v1
```

### 1. Buscar Pessoas com Contas

**POST** `/pessoas-contas/buscar`

**Request Body:**
```json
{
  "ano": 2024,
  "mesInicial": 1,
  "mesFinal": 6,
  "limit": 100,
  "offset": 0
}
```

**Response:**
```json
{
  "success": true,
  "message": "Pessoas encontradas com sucesso",
  "data": [
    {
      "idPessoa": 123,
      "documento": "12345678900",
      "nome": "João Silva",
      "cpf": "12345678900",
      "nacionalidade": "BR",
      "telefone": "11999999999",
      "email": "joao@email.com",
      "idConta": 456,
      "numeroConta": "12345",
      "digitoConta": "6",
      "saldoAtual": 1000.50,
      "logradouro": "Rua Exemplo",
      "numero": "123",
      "complemento": "Apto 45",
      "bairro": "Centro",
      "cep": "01234567",
      "tipoLogradouro": "RUA",
      "enderecoLivre": "",
      "totCreditos": 5000.00,
      "totDebitos": 4000.00
    }
  ],
  "timestamp": "2024-01-15T10:30:00"
}
```

### 2. Calcular Totais de Movimentação

**POST** `/pessoas-contas/totais-movimentacao`

**Request Body:**
```json
{
  "idConta": 456,
  "ano": 2024,
  "mesInicial": 1,
  "mesFinal": 6
}
```

**Response:**
```json
{
  "success": true,
  "message": "Totais calculados com sucesso",
  "data": {
    "totCreditos": 5000.00,
    "totDebitos": 4000.00
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 3. Buscar Lotes

**POST** `/lotes/buscar`

**Request Body:**
```json
{
  "dataInicio": "2024-01-01T00:00:00",
  "dataFim": "2024-01-31T23:59:59",
  "periodo": "202401",
  "ambiente": "PROD",
  "limite": 50
}
```

**Response:**
```json
{
  "success": true,
  "message": "Lotes encontrados com sucesso",
  "data": [
    {
      "idLote": 789,
      "periodo": "202401",
      "semestre": 1,
      "numeroLote": 1,
      "quantidadeEventos": 100,
      "cnpjDeclarante": "12345678000190",
      "protocoloEnvio": "PROT123456",
      "status": "ENVIADO",
      "ambiente": "PROD",
      "dataCriacao": "2024-01-15T10:00:00",
      "tipoLote": "MOVIMENTACAO",
      "totalEventosRegistrados": 100,
      "totalEventosComCpf": 95,
      "totalEventosComErro": 5,
      "totalEventosSucesso": 90,
      "ehRetificacao": false
    }
  ],
  "timestamp": "2024-01-15T10:30:00"
}
```

### 4. Buscar Lote por Protocolo

**GET** `/lotes/protocolo/{protocolo}`

**Response:**
```json
{
  "success": true,
  "message": "Lote encontrado com sucesso",
  "data": {
    "idLote": 789,
    "protocoloEnvio": "PROT123456",
    "status": "ENVIADO",
    ...
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 5. Buscar Eventos do Lote

**GET** `/lotes/{idLote}/eventos`

**Response:**
```json
{
  "success": true,
  "message": "Eventos encontrados com sucesso",
  "data": [
    {
      "idEvento": 1001,
      "idLote": 789,
      "idPessoa": 123,
      "cpf": "12345678900",
      "nome": "João Silva",
      "statusEvento": "SUCESSO",
      ...
    }
  ],
  "timestamp": "2024-01-15T10:30:00"
}
```

### 6. Verificar Abertura Enviada

**GET** `/lotes/verificar-abertura?periodo=202401&ambiente=PROD`

**Response:**
```json
{
  "success": true,
  "message": "Verificação realizada com sucesso",
  "data": true,
  "timestamp": "2024-01-15T10:30:00"
}
```

## 🎯 Princípios Aplicados

### SOLID
- **S**ingle Responsibility: Cada classe tem uma única responsabilidade
- **O**pen/Closed: Aberto para extensão, fechado para modificação
- **L**iskov Substitution: Subtipos são substituíveis por seus tipos base
- **I**nterface Segregation: Interfaces específicas ao invés de genéricas
- **D**ependency Inversion: Dependências apontam para abstrações

### Object Calisthenics
- Um nível de indentação por método
- Não use ELSE
- Encapsule primitivos e strings
- Coleções de primeira classe
- Um ponto por linha
- Não abrevie
- Mantenha entidades pequenas
- Não use mais de duas variáveis de instância

## 📊 Performance

A API foi desenvolvida com foco em performance:

- **Consultas SQL otimizadas**: Uso de índices e agregações eficientes
- **Connection Pooling**: Configuração do HikariCP para gerenciamento de conexões
- **Read-Only Transactions**: Transações somente leitura para consultas
- **Lazy Loading**: Carregamento sob demanda quando aplicável
- **Batch Processing**: Processamento em lote quando necessário

## 🔍 Logging

O sistema utiliza **Aspect-Oriented Programming (AOP)** para logging automático:

- Log de entrada e saída de métodos
- Medição de tempo de execução
- Log de erros com stack trace
- Níveis de log configuráveis

## 🛡️ Tratamento de Erros

A API possui tratamento centralizado de exceções:

- Validação de parâmetros de entrada
- Mensagens de erro padronizadas
- Códigos HTTP apropriados
- Logging de erros para diagnóstico

## 📝 Validações

Todas as requisições são validadas usando Bean Validation:

- Validação de campos obrigatórios
- Validação de ranges e formatos
- Mensagens de erro descritivas

## 🧪 Testes

Para executar os testes:

```bash
mvn test
```

## 📦 Build

Para gerar o artefato JAR:

```bash
mvn clean package
```

O arquivo será gerado em: `target/efinanceira-api-1.0.0.jar`

## 🚀 Deploy

Para executar o JAR gerado:

```bash
java -jar target/efinanceira-api-1.0.0.jar
```

## 📄 Licença

Este projeto é de uso interno da BSCash.

## 👥 Autor

Desenvolvido para centralizar e otimizar consultas ao banco de dados do sistema eFinanceira.

---

**Versão**: 1.0.0  
**Última atualização**: 2024
