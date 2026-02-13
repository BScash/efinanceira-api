package br.com.bscash.efinanceira.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarEventoRequest {
    @NotNull(message = "ID do lote é obrigatório")
    @Positive(message = "ID do lote deve ser maior que 0")
    private Long idLote;
    
    private Long idPessoa;
    private Long idConta;
    private String cpf;
    private String nome;
    private Long numeroConta;
    private String digitoConta;
    private BigDecimal saldoAtual;
    private BigDecimal totCreditos;
    private BigDecimal totDebitos;
    private String idEventoXml;
    private String statusEvento;
    private String ocorrenciasJson;
    private String numeroRecibo;
    private Integer indRetificacao;
}
