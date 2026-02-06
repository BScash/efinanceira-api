# Scripts de Migração - Campos de Auditoria eFinanceira

## Visão Geral

Estes scripts adicionam os campos de auditoria padrão do sistema em todas as tabelas do schema `efinanceira`:

- `situacao` - Status do registro (VARCHAR(1), padrão '1')
- `idusuarioinclusao` - ID do usuário que criou o registro (BIGINT, padrão 1)
- `idusuarioalteracao` - ID do usuário que alterou o registro (BIGINT, nullable)
- `idusuarioalteracaosituacao` - ID do usuário que alterou a situação (BIGINT, nullable)
- `datainclusao` - Data de criação do registro (TIMESTAMP, padrão CURRENT_TIMESTAMP)
- `dataalteracao` - Data da última alteração (TIMESTAMP, nullable)
- `dataalteracaosituacao` - Data da alteração de situação (TIMESTAMP, nullable)

## Tabelas Afetadas

1. `efinanceira.tb_efinanceira_lote`
2. `efinanceira.tb_efinanceira_evento`
3. `efinanceira.tb_efinanceira_lote_log`
4. `efinanceira.tb_efinanceira_arquivo_s3`

## Scripts Disponíveis

### 1. `01_create_schema_homol_completo.sql`

**Uso:** Criação inicial do schema em homologação

**Conteúdo:**
- Cria todas as tabelas com os campos de auditoria já incluídos
- Cria todas as sequences necessárias
- Cria todas as foreign keys (incluindo as de auditoria)
- Cria todos os índices de otimização

**Quando usar:**
- Primeira instalação em homologação
- Criação de novo ambiente de teste

### 2. `02_migracao_auditoria_dev.sql`

**Uso:** Migração de tabelas existentes em desenvolvimento

**Conteúdo:**
- Adiciona os campos de auditoria nas tabelas existentes
- Migra dados existentes (datacriacao -> datainclusao, dataatualizacao -> dataalteracao)
- Cria foreign keys de auditoria
- Cria índices de otimização

**Quando usar:**
- Atualização do banco de desenvolvimento
- Migração de ambiente existente

## Foreign Keys Criadas

Todas as tabelas terão foreign keys para `controleacesso.tb_usuario(idusuario)`:

- `tb_efinanceira_lote_usuario_inclusao_fk`
- `tb_efinanceira_lote_usuario_alteracao_fk`
- `tb_efinanceira_lote_usuario_alteracao_situacao_fk`
- `tb_efinanceira_evento_usuario_inclusao_fk`
- `tb_efinanceira_evento_usuario_alteracao_fk`
- `tb_efinanceira_evento_usuario_alteracao_situacao_fk`
- `tb_efinanceira_lote_log_usuario_inclusao_fk`
- `tb_efinanceira_lote_log_usuario_alteracao_fk`
- `tb_efinanceira_lote_log_usuario_alteracao_situacao_fk`
- `tb_efinanceira_arquivo_s3_usuario_inclusao_fk`
- `tb_efinanceira_arquivo_s3_usuario_alteracao_fk`
- `tb_efinanceira_arquivo_s3_usuario_alteracao_situacao_fk`

## Índices Criados

Para cada tabela, são criados os seguintes índices:

- `idx_{tabela}_situacao` - Para filtros por situação
- `idx_{tabela}_usuario_inclusao` - Para consultas por usuário que criou
- `idx_{tabela}_datainclusao` - Para ordenação por data de criação

## Migração de Dados

O script de migração (`02_migracao_auditoria_dev.sql`) faz as seguintes migrações automáticas:

- **tb_efinanceira_lote:** 
  - `datainclusao` = `datacriacao` (se datainclusao for NULL ou menor)
  - `dataalteracao` = `dataatualizacao` (se dataatualizacao existir)

- **tb_efinanceira_evento:**
  - `datainclusao` = `datacriacao` (se datainclusao for NULL ou menor)

- **tb_efinanceira_lote_log:**
  - `datainclusao` = `timestamp` (se datainclusao for NULL ou menor)

- **tb_efinanceira_arquivo_s3:**
  - `datainclusao` = `data_upload` (se datainclusao for NULL ou menor)
  - `dataalteracao` = `data_atualizacao` (se data_atualizacao existir)

## Observações Importantes

1. **Campos Existentes:** Os campos `datacriacao` e `dataatualizacao` continuam existindo. Os novos campos de auditoria (`datainclusao` e `dataalteracao`) são adicionados separadamente para manter compatibilidade.

2. **Valores Padrão:** 
   - `situacao` = '1' (ativo)
   - `idusuarioinclusao` = 1 (usuário sistema)
   - `datainclusao` = CURRENT_TIMESTAMP

3. **Foreign Keys:** Todas as foreign keys referenciam `controleacesso.tb_usuario(idusuario)`. Certifique-se de que esta tabela existe antes de executar os scripts.

4. **Idempotência:** Ambos os scripts são idempotentes - podem ser executados múltiplas vezes sem causar erros.

## Verificação Pós-Migração

Os scripts incluem queries de verificação no final. Execute-as para confirmar que:

1. Todos os campos foram criados
2. Todas as foreign keys foram criadas
3. Todos os índices foram criados

## Rollback

Cada script inclui uma seção comentada com os comandos de rollback caso seja necessário reverter a migração.

## Próximos Passos

Após executar os scripts:

1. Atualizar as entidades Java para incluir os novos campos
2. Atualizar os repositórios para trabalhar com os campos de auditoria
3. Implementar a lógica de auditoria no backoffice (se necessário)
