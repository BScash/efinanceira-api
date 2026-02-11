package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import br.com.bscash.efinanceira.infrastructure.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventoService {
    
    private final EventoRepository repository;
    
    public List<EventoBancoInfo> buscarEventos(Long idLote, Long idPessoa, Long idConta,
                                                String cpf, String nome, String statusEvento,
                                                String numeroRecibo, Boolean ehRetificacao,
                                                LocalDateTime dataInicio, LocalDateTime dataFim,
                                                Integer limite, Integer offset) {
        return repository.buscarEventos(idLote, idPessoa, idConta, cpf, nome, statusEvento,
                                        numeroRecibo, ehRetificacao, dataInicio, dataFim,
                                        limite, offset);
    }
}
