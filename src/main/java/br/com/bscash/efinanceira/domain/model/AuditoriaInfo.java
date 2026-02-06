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
public class AuditoriaInfo {

    private String situacao;
    
    private Long idUsuarioInclusao;

    private Long idUsuarioAlteracao;

    private Long idUsuarioAlteracaoSituacao;
    
    private LocalDateTime dataInclusao;
    
    private LocalDateTime dataAlteracao;
    
    private LocalDateTime dataAlteracaoSituacao;
    
    public static AuditoriaInfo paraInclusao(Long idUsuarioInclusao) {
        return AuditoriaInfo.builder()
                .situacao("1")
                .idUsuarioInclusao(idUsuarioInclusao)
                .dataInclusao(LocalDateTime.now())
                .build();
    }
    
    public static AuditoriaInfo paraAlteracao(Long idUsuarioAlteracao) {
        return AuditoriaInfo.builder()
                .idUsuarioAlteracao(idUsuarioAlteracao)
                .dataAlteracao(LocalDateTime.now())
                .build();
    }
    
    public static AuditoriaInfo paraAlteracaoSituacao(Long idUsuarioAlteracaoSituacao, String novaSituacao) {
        return AuditoriaInfo.builder()
                .situacao(novaSituacao)
                .idUsuarioAlteracaoSituacao(idUsuarioAlteracaoSituacao)
                .dataAlteracaoSituacao(LocalDateTime.now())
                .build();
    }
}
