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
public class LotesRequest {
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String periodo;
    private String ambiente;
    private Integer limite;
}
