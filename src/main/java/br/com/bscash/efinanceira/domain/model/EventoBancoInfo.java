package br.com.bscash.efinanceira.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoBancoInfo {
    private Long idEvento;
    private Long idLote;
    private Long idPessoa;
    private Long idConta;
    private String cpf;
    private String nome;
    private Long numeroConta;
    private String digitoConta;
    private BigDecimal saldoAtual;
    private BigDecimal totCreditos;
    private BigDecimal totDebitos;
    private String idEventoXml;
    private String statusEvento;
    private String ocorrenciasJson;
    private LocalDateTime dataCriacao;
    private String numeroRecibo;
    private Integer indRetificacao;
    private Boolean ehRetificacao;
    
    private String situacao;
    private Long idUsuarioInclusao;
    private Long idUsuarioAlteracao;
    private Long idUsuarioAlteracaoSituacao;
    private LocalDateTime dataInclusao;
    private LocalDateTime dataAlteracao;
    private LocalDateTime dataAlteracaoSituacao;
}
