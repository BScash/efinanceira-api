package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.domain.model.DadosPessoaConta;
import br.com.bscash.efinanceira.domain.model.TotaisMovimentacao;
import br.com.bscash.efinanceira.infrastructure.repository.PessoaContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PessoaContaService {
    
    private final PessoaContaRepository repository;
    
    public List<DadosPessoaConta> buscarPessoasComContas(Integer ano, Integer mesInicial, 
                                                          Integer mesFinal, Integer limit, Integer offset) {
        validarParametrosBusca(ano, mesInicial, mesFinal, limit, offset);
        return repository.buscarPessoasComContas(ano, mesInicial, mesFinal, limit, offset);
    }
    
    public TotaisMovimentacao calcularTotaisMovimentacao(Long idConta, Integer ano, 
                                                          Integer mesInicial, Integer mesFinal) {
        validarParametrosTotais(idConta, ano, mesInicial, mesFinal);
        return repository.calcularTotaisMovimentacao(idConta, ano, mesInicial, mesFinal);
    }
    
    private void validarParametrosBusca(Integer ano, Integer mesInicial, Integer mesFinal, 
                                        Integer limit, Integer offset) {
        if (mesInicial < 1 || mesInicial > 12) {
            throw new IllegalArgumentException("Mês inicial inválido: " + mesInicial + ". Deve estar entre 1 e 12.");
        }
        if (mesFinal < 1 || mesFinal > 12) {
            throw new IllegalArgumentException("Mês final inválido: " + mesFinal + ". Deve estar entre 1 e 12.");
        }
        if (mesInicial > mesFinal) {
            throw new IllegalArgumentException(
                String.format("Mês inicial (%d) não pode ser maior que mês final (%d). " +
                    "Para períodos semestrais, use: 1-6 (Jan-Jun) ou 7-12 (Jul-Dez).", 
                    mesInicial, mesFinal));
        }
        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("Limit deve ser maior que 0.");
        }
        if (offset != null && offset < 0) {
            throw new IllegalArgumentException("Offset deve ser maior ou igual a 0.");
        }
    }
    
    private void validarParametrosTotais(Long idConta, Integer ano, Integer mesInicial, Integer mesFinal) {
        if (idConta == null || idConta <= 0) {
            throw new IllegalArgumentException("ID da conta é obrigatório e deve ser maior que 0.");
        }
        if (mesInicial < 1 || mesInicial > 12) {
            throw new IllegalArgumentException("Mês inicial inválido: " + mesInicial + ". Deve estar entre 1 e 12.");
        }
        if (mesFinal < 1 || mesFinal > 12) {
            throw new IllegalArgumentException("Mês final inválido: " + mesFinal + ". Deve estar entre 1 e 12.");
        }
        if (mesInicial > mesFinal) {
            throw new IllegalArgumentException(
                String.format("Mês inicial (%d) não pode ser maior que mês final (%d).", mesInicial, mesFinal));
        }
    }
}
