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
public class EventosRequest {
    private Long idLote;
    private Long idPessoa;
    private Long idConta;
    private String cpf;
    private String nome;
    private String statusEvento;
    private String numeroRecibo;
    private Boolean ehRetificacao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Integer limite;
    private Integer offset;
}
