package br.com.bscash.efinanceira.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarLoteRequest {
    private String status;
    private String protocoloEnvio;
    private Integer codigoRespostaEnvio;
    private String descricaoRespostaEnvio;
    private String xmlRespostaEnvio;
    private Integer codigoRespostaConsulta;
    private String descricaoRespostaConsulta;
    private String xmlRespostaConsulta;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataConfirmacao;
    private String ultimoErro;
    private String caminhoArquivoAssinado;
    private String caminhoArquivoCriptografado;
}
