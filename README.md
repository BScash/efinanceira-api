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
- **Spring Security**: Autenticação e autorização
- **JWT (JSON Web Token)**: Tokens de autenticação
- **BCrypt**: Criptografia de senhas
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
│   │   │   │   ├── config/           # Configurações (Security, CORS, etc)
│   │   │   │   ├── controller/       # Controllers REST
│   │   │   │   └── exception/        # Tratamento de exceções
│   │   │   ├── domain/                # Camada de domínio
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── model/            # Modelos de domínio
│   │   │   │   └── service/          # Serviços de negócio
│   │   │   └── infrastructure/       # Camada de infraestrutura
│   │   │       ├── repository/       # Repositórios de dados
│   │   │       └── util/             # Utilitários
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

1. **Application**: Controllers, aspectos, configurações e tratamento de exceções
2. **Domain**: Lógica de negócio, modelos e DTOs
3. **Infrastructure**: Acesso a dados (repositórios) e utilitários

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

### Configuração de Autenticação

A API utiliza autenticação JWT **independente e autônoma**. Configure as seguintes propriedades:

```yaml
jwt:
  token:
    secret: ${JWT_SECRET:seu-secret-key}
    expiration-time-in-minutes: ${JWT_EXPIRATION:1000}
```

**Variáveis de Ambiente:**
- `JWT_SECRET`: Secret key para geração e validação de tokens JWT
- `JWT_EXPIRATION`: Tempo de expiração do token em minutos (padrão: 1000)

### Executando a Aplicação

```bash
# Compilar o projeto
mvn clean install

# Executar a aplicação
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 🔐 Autenticação e Segurança

### Como Funciona a Autenticação

A efinanceira-api utiliza autenticação JWT **completamente autônoma**, sem dependência de serviços externos. Todas as APIs protegidas requerem um token JWT válido obtido através do endpoint de autenticação.

#### Fluxo de Autenticação

1. **Obter Token**: O cliente faz uma requisição POST para `/auth` com login e senha
2. **Validação Local**: A efinanceira-api busca o usuário no banco de dados (`controleacesso.tb_usuario`)
3. **Validação de Senha**: A senha é validada usando BCrypt (mesmo algoritmo usado no backoffice)
4. **Geração de Token**: Se as credenciais forem válidas, um token JWT é gerado localmente
5. **Uso do Token**: O cliente utiliza o token no header `Authorization: Bearer <token>` em todas as requisições protegidas

#### Validações Realizadas

Durante a autenticação, o sistema verifica:
- ✅ Existência do usuário no banco de dados
- ✅ Usuário está ativo (situação = '1')
- ✅ Usuário não está bloqueado
- ✅ Senha está cadastrada
- ✅ Senha informada corresponde à senha criptografada (BCrypt)

#### Proteção das APIs

Todas as APIs dos controllers `LoteController` e `PessoaContaController` estão **protegidas** e requerem autenticação:

- ✅ **Protegidas (requerem token)**:
  - `POST /api/v1/lotes/buscar`
  - `GET /api/v1/lotes/protocolo/{protocolo}`
  - `GET /api/v1/lotes/{idLote}/eventos`
  - `GET /api/v1/lotes/verificar-abertura`
  - `POST /api/v1/pessoas-contas/buscar`
  - `POST /api/v1/pessoas-contas/totais-movimentacao`

- 🔓 **Públicas (não requerem token)**:
  - `POST /auth` - Endpoint de autenticação
  - `GET /actuator/**` - Endpoints de monitoramento

#### Como Testar a Autenticação

**1. Autenticar e obter token:**

```bash
curl --location --request POST 'http://localhost:8080/auth' \
--header 'Content-Type: application/json' \
--data-raw '{
    "login": "seu_login",
    "senha": "sua_senha"
}'
```

**Resposta esperada:**
```json
{
    "success": true,
    "message": "Autenticação realizada com sucesso",
    "data": {
        "refreshToken": "uuid-refresh-token",
        "accessToken": {
            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "type": "Bearer"
        }
    },
    "timestamp": "2024-01-15T10:30:00"
}
```

**2. Usar o token em requisições protegidas:**

```bash
# Exemplo: Buscar lotes
curl --location --request POST 'http://localhost:8080/api/v1/lotes/buscar' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...' \
--data-raw '{
    "dataInicio": "2024-01-01T00:00:00",
    "dataFim": "2024-01-31T23:59:59",
    "periodo": "202401",
    "ambiente": "PROD",
    "limite": 50
}'
```

**3. Testar sem token (deve retornar 401 Unauthorized):**

```bash
curl --location --request POST 'http://localhost:8080/api/v1/lotes/buscar' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dataInicio": "2024-01-01T00:00:00",
    "dataFim": "2024-01-31T23:59:59"
}'
```

**Resposta esperada (401 Unauthorized):**
```json
{
    "success": false,
    "message": "Token inválido ou ausente. Autenticação necessária.",
    "data": null,
    "timestamp": "2024-01-15T10:30:00"
}
```

**4. Testar com token inválido (deve retornar 401 Unauthorized):**

```bash
curl --location --request POST 'http://localhost:8080/api/v1/lotes/buscar' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer token-invalido' \
--data-raw '{
    "dataInicio": "2024-01-01T00:00:00",
    "dataFim": "2024-01-31T23:59:59"
}'
```

#### Arquitetura de Segurança

A segurança é implementada através de:

1. **AutenticacaoViaTokenFilter**: Filtro que intercepta todas as requisições e valida o token JWT no header `Authorization`
2. **TokenService**: Serviço responsável por gerar, validar tokens JWT e extrair informações do usuário autenticado
3. **AuthenticationService**: Serviço que realiza a autenticação local, validando credenciais no banco de dados
4. **UsuarioRepository**: Repositório que busca usuários na tabela `controleacesso.tb_usuario`
5. **WebSecurityConfiguration**: Configuração do Spring Security que define quais endpoints são públicos e quais requerem autenticação
6. **CustomAuthenticationEntryPoint**: Tratamento de erros de autenticação, retornando respostas padronizadas

#### Validação do Token

O token JWT é validado verificando:
- ✅ Assinatura do token (usando o secret configurado)
- ✅ Expiração do token
- ✅ Formato e estrutura do token
- ✅ Presença do ID do usuário no subject do token

#### Criptografia de Senhas

As senhas são armazenadas no banco de dados usando **BCrypt**, o mesmo algoritmo utilizado no backoffice, garantindo compatibilidade total. A validação é feita localmente, sem necessidade de comunicação com serviços externos.

## 📚 Endpoints da API

### Base URL
```
http://localhost:8080/api/v1
```

### 🔐 Autenticação

**POST** `/auth`

**Headers:**
- `Content-Type: application/json`

**Request Body:**
```json
{
  "login": "seu_login",
  "senha": "sua_senha"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Autenticação realizada com sucesso",
  "data": {
    "refreshToken": "uuid-refresh-token",
    "accessToken": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "type": "Bearer"
    }
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**⚠️ Nota**: Este endpoint é público e não requer autenticação.

**Possíveis Erros:**
- `400 Bad Request`: Credenciais inválidas (usuário não encontrado ou senha incorreta)
- `400 Bad Request`: Usuário inativo
- `400 Bad Request`: Usuário bloqueado
- `400 Bad Request`: Senha não cadastrada

---

### 1. Buscar Pessoas com Contas

**POST** `/api/v1/pessoas-contas/buscar`

**🔒 Requer Autenticação**: Sim (Bearer Token)

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

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

**POST** `/api/v1/pessoas-contas/totais-movimentacao`

**🔒 Requer Autenticação**: Sim (Bearer Token)

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

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

**POST** `/api/v1/lotes/buscar`

**🔒 Requer Autenticação**: Sim (Bearer Token)

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

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

**GET** `/api/v1/lotes/protocolo/{protocolo}`

**🔒 Requer Autenticação**: Sim (Bearer Token)

**Headers:**
- `Authorization: Bearer <token>`

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

**GET** `/api/v1/lotes/{idLote}/eventos`

**🔒 Requer Autenticação**: Sim (Bearer Token)

**Headers:**
- `Authorization: Bearer <token>`

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

**GET** `/api/v1/lotes/verificar-abertura?periodo=202401&ambiente=PROD`

**🔒 Requer Autenticação**: Sim (Bearer Token)

**Headers:**
- `Authorization: Bearer <token>`

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

## 🔄 Mudanças Recentes

### Versão Atual - Autenticação Autônoma

- ✅ **Autenticação independente**: A API agora realiza autenticação localmente, sem depender do backoffice
- ✅ **Validação BCrypt**: Senhas são validadas usando BCrypt, garantindo compatibilidade com o backoffice
- ✅ **Geração de tokens local**: Tokens JWT são gerados diretamente na API
- ✅ **Remoção de dependências**: Removida a dependência do Spring WebFlux e comunicação HTTP com backoffice
- ✅ **Repositório de usuários**: Implementado `UsuarioRepository` para buscar usuários no banco de dados

## 📄 Licença

Este projeto é de uso interno da BSCash.

## 👥 Autor

Desenvolvido para centralizar e otimizar consultas ao banco de dados do sistema eFinanceira.

---

**Versão**: 1.0.0  
**Última atualização**: 2024
