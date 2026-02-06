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
public class LoteBancoInfo {
    private Long idLote;
    private String periodo;
    private Integer semestre;
    private Integer numeroLote;
    private Integer quantidadeEventos;
    private String cnpjDeclarante;
    private String protocoloEnvio;
    private String status;
    private String ambiente;
    private Integer codigoRespostaEnvio;
    private String descricaoRespostaEnvio;
    private Integer codigoRespostaConsulta;
    private String descricaoRespostaConsulta;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataConfirmacao;
    private Long idLoteOriginal;
    private String caminhoArquivoXml;
    private String tipoLote;
    private Integer totalEventosRegistrados;
    private Integer totalEventosComCpf;
    private Integer totalEventosComErro;
    private Integer totalEventosSucesso;
    private Boolean ehRetificacao;
    
    private String situacao;
    private Long idUsuarioInclusao;
    private Long idUsuarioAlteracao;
    private Long idUsuarioAlteracaoSituacao;
    private LocalDateTime dataInclusao;
    private LocalDateTime dataAlteracao;
    private LocalDateTime dataAlteracaoSituacao;
}
