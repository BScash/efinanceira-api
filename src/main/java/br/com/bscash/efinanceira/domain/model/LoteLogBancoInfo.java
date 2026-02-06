package br.com.bscash.efinanceira.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoteLogBancoInfo {
    private Long idLog;
    private Long idLote;
    private String etapa;
    private String mensagem;
    private String payloadCurto;
    private LocalDateTime timestamp;
    
    private String situacao;
    private Long idUsuarioInclusao;
    private Long idUsuarioAlteracao;
    private Long idUsuarioAlteracaoSituacao;
    private LocalDateTime dataInclusao;
    private LocalDateTime dataAlteracao;
    private LocalDateTime dataAlteracaoSituacao;
}
