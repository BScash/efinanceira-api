package br.com.bscash.efinanceira.application.controller;

import br.com.bscash.efinanceira.domain.dto.ApiResponse;
import br.com.bscash.efinanceira.domain.dto.EventosRequest;
import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import br.com.bscash.efinanceira.domain.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
public class EventoController {
    
    private final EventoService service;
    
    @PostMapping("/buscar")
    public ResponseEntity<ApiResponse<List<EventoBancoInfo>>> buscarEventos(
            @RequestBody(required = false) EventosRequest request) {
        if (request == null) {
            request = EventosRequest.builder().build();
        }
        
        List<EventoBancoInfo> eventos = service.buscarEventos(
            request.getIdLote(),
            request.getIdPessoa(),
            request.getIdConta(),
            request.getCpf(),
            request.getNome(),
            request.getStatusEvento(),
            request.getNumeroRecibo(),
            request.getEhRetificacao(),
            request.getDataInicio(),
            request.getDataFim(),
            request.getLimite() != null ? request.getLimite() : 100,
            request.getOffset() != null ? request.getOffset() : 0
        );
        
        return ResponseEntity.ok(ApiResponse.success("Eventos encontrados com sucesso", eventos));
    }
}
