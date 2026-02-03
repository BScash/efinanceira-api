package br.com.bscash.efinanceira.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotaisMovimentacaoRequest {
    @NotNull(message = "ID da conta é obrigatório")
    @Min(value = 1, message = "ID da conta deve ser maior que 0")
    private Long idConta;
    
    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2000, message = "Ano deve ser maior ou igual a 2000")
    @Max(value = 2100, message = "Ano deve ser menor ou igual a 2100")
    private Integer ano;
    
    @NotNull(message = "Mês inicial é obrigatório")
    @Min(value = 1, message = "Mês inicial deve estar entre 1 e 12")
    @Max(value = 12, message = "Mês inicial deve estar entre 1 e 12")
    private Integer mesInicial;
    
    @NotNull(message = "Mês final é obrigatório")
    @Min(value = 1, message = "Mês final deve estar entre 1 e 12")
    @Max(value = 12, message = "Mês final deve estar entre 1 e 12")
    private Integer mesFinal;
}
