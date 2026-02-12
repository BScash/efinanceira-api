package br.com.bscash.efinanceira.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarEventoRequest {
    private String statusEvento;
    private String ocorrenciasJson;
    private String numeroRecibo;
}
