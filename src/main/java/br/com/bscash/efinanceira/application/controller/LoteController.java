package br.com.bscash.efinanceira.application.controller;

import br.com.bscash.efinanceira.domain.dto.ApiResponse;
import br.com.bscash.efinanceira.domain.dto.LotesRequest;
import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import br.com.bscash.efinanceira.domain.model.LoteBancoInfo;
import br.com.bscash.efinanceira.domain.service.LoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lotes")
@RequiredArgsConstructor
public class LoteController {
    
    private final LoteService service;
    
    @PostMapping("/buscar")
    public ResponseEntity<ApiResponse<List<LoteBancoInfo>>> buscarLotes(
            @RequestBody(required = false) LotesRequest request) {
        if (request == null) {
            request = LotesRequest.builder().build();
        }
        
        List<LoteBancoInfo> lotes = service.buscarLotes(
            request.getDataInicio(),
            request.getDataFim(),
            request.getPeriodo(),
            request.getAmbiente(),
            request.getLimite()
        );
        return ResponseEntity.ok(ApiResponse.success("Lotes encontrados com sucesso", lotes));
    }
    
    @GetMapping("/protocolo/{protocolo}")
    public ResponseEntity<ApiResponse<LoteBancoInfo>> buscarLotePorProtocolo(
            @PathVariable String protocolo) {
        LoteBancoInfo lote = service.buscarLotePorProtocolo(protocolo);
        if (lote == null) {
            return ResponseEntity.ok(ApiResponse.error("Lote não encontrado para o protocolo informado"));
        }
        return ResponseEntity.ok(ApiResponse.success("Lote encontrado com sucesso", lote));
    }
    
    @GetMapping("/{idLote}/eventos")
    public ResponseEntity<ApiResponse<List<EventoBancoInfo>>> buscarEventosDoLote(
            @PathVariable Long idLote) {
        List<EventoBancoInfo> eventos = service.buscarEventosDoLote(idLote);
        return ResponseEntity.ok(ApiResponse.success("Eventos encontrados com sucesso", eventos));
    }
    
    @GetMapping("/verificar-abertura")
    public ResponseEntity<ApiResponse<Boolean>> verificarAberturaEnviadaParaPeriodo(
            @RequestParam String periodo,
            @RequestParam String ambiente) {
        boolean existe = service.verificarAberturaEnviadaParaPeriodo(periodo, ambiente);
        return ResponseEntity.ok(ApiResponse.success("Verificação realizada com sucesso", existe));
    }
}
