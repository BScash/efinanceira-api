package br.com.bscash.efinanceira.application.controller;

import br.com.bscash.efinanceira.domain.dto.ApiResponse;
import br.com.bscash.efinanceira.domain.dto.PessoasComContasRequest;
import br.com.bscash.efinanceira.domain.dto.TotaisMovimentacaoRequest;
import br.com.bscash.efinanceira.domain.model.DadosPessoaConta;
import br.com.bscash.efinanceira.domain.model.TotaisMovimentacao;
import br.com.bscash.efinanceira.domain.service.PessoaContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pessoas-contas")
@RequiredArgsConstructor
public class PessoaContaController {
    
    private final PessoaContaService service;
    
    @PostMapping("/buscar")
    public ResponseEntity<ApiResponse<List<DadosPessoaConta>>> buscarPessoasComContas(
            @Valid @RequestBody PessoasComContasRequest request) {
        List<DadosPessoaConta> pessoas = service.buscarPessoasComContas(
            request.getAno(),
            request.getMesInicial(),
            request.getMesFinal(),
            request.getLimit() != null ? request.getLimit() : 100,
            request.getOffset() != null ? request.getOffset() : 0
        );
        return ResponseEntity.ok(ApiResponse.success("Pessoas encontradas com sucesso", pessoas));
    }
    
    @PostMapping("/totais-movimentacao")
    public ResponseEntity<ApiResponse<TotaisMovimentacao>> calcularTotaisMovimentacao(
            @Valid @RequestBody TotaisMovimentacaoRequest request) {
        TotaisMovimentacao totais = service.calcularTotaisMovimentacao(
            request.getIdConta(),
            request.getAno(),
            request.getMesInicial(),
            request.getMesFinal()
        );
        return ResponseEntity.ok(ApiResponse.success("Totais calculados com sucesso", totais));
    }
}
