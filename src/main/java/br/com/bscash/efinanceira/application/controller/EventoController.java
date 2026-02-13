package br.com.bscash.efinanceira.application.controller;

import br.com.bscash.efinanceira.domain.dto.ApiResponse;
import br.com.bscash.efinanceira.domain.dto.AtualizarEventoRequest;
import br.com.bscash.efinanceira.domain.dto.EventosRequest;
import br.com.bscash.efinanceira.domain.dto.RegistrarEventoRequest;
import br.com.bscash.efinanceira.domain.dto.RegistrarEventosRequest;
import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import br.com.bscash.efinanceira.domain.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        
        List<EventoBancoInfo> eventos = service.buscarEventos(request);
        
        return ResponseEntity.ok(ApiResponse.success("Eventos encontrados com sucesso", eventos));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> registrarEvento(
            @Valid @RequestBody RegistrarEventoRequest request) {
        Long idEvento = service.registrarEvento(request);
        Map<String, Long> response = new HashMap<>();
        response.put("idEvento", idEvento);
        return ResponseEntity.ok(ApiResponse.success("Evento registrado com sucesso", response));
    }
    
    @PostMapping("/lote")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registrarEventosDoLote(
            @Valid @RequestBody RegistrarEventosRequest request) {
        List<Long> idsEventos = service.registrarEventosDoLote(request);
        Map<String, Object> response = new HashMap<>();
        response.put("idsEventos", idsEventos);
        response.put("quantidadeRegistrada", idsEventos.size());
        return ResponseEntity.ok(ApiResponse.success("Eventos registrados com sucesso", response));
    }
    
    @PutMapping("/{idEvento}")
    public ResponseEntity<ApiResponse<Void>> atualizarEvento(
            @PathVariable Long idEvento,
            @Valid @RequestBody AtualizarEventoRequest request) {
        service.atualizarEvento(idEvento, request);
        return ResponseEntity.ok(ApiResponse.success("Evento atualizado com sucesso", null));
    }
}
