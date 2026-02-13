package br.com.bscash.efinanceira.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarEventosRequest {
    @NotEmpty(message = "Lista de eventos não pode estar vazia")
    @Valid
    private List<RegistrarEventoRequest> eventos;
    
    private String idEventoPrefix;
    private Integer indRetificacao;
}
