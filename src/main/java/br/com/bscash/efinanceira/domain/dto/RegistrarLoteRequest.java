package br.com.bscash.efinanceira.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarLoteRequest {
    private String periodo;
    private Integer quantidadeEventos;
    private String cnpjDeclarante;
    private String caminhoArquivoXml;
    private String caminhoArquivoAssinado;
    private String caminhoArquivoCriptografado;
    private String ambiente;
    private Integer numeroLote;
    private Long idLoteOriginal;
}
