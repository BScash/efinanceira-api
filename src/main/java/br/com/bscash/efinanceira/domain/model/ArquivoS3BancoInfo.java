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
public class ArquivoS3BancoInfo {
    private Long idArquivo;
    private String caminho;
    private String hashSha256;
    private Long tamanhoBytes;
    private LocalDateTime dataUpload;
    private LocalDateTime dataAtualizacao;
    private Boolean verificado;
    private LocalDateTime dataVerificacao;
    
    private String situacao;
    private Long idUsuarioInclusao;
    private Long idUsuarioAlteracao;
    private Long idUsuarioAlteracaoSituacao;
    private LocalDateTime dataInclusao;
    private LocalDateTime dataAlteracao;
    private LocalDateTime dataAlteracaoSituacao;
}
