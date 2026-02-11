package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.domain.dto.RegistrarEventoRequest;
import br.com.bscash.efinanceira.domain.dto.RegistrarEventosRequest;
import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import br.com.bscash.efinanceira.infrastructure.repository.EventoRepository;
import br.com.bscash.efinanceira.infrastructure.util.AuditoriaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {
    
    private final EventoRepository repository;
    
    @Transactional(readOnly = true)
    public List<EventoBancoInfo> buscarEventos(Long idLote, Long idPessoa, Long idConta,
                                                String cpf, String nome, String statusEvento,
                                                String numeroRecibo, Boolean ehRetificacao,
                                                LocalDateTime dataInicio, LocalDateTime dataFim,
                                                Integer limite, Integer offset) {
        return repository.buscarEventos(idLote, idPessoa, idConta, cpf, nome, statusEvento,
                                        numeroRecibo, ehRetificacao, dataInicio, dataFim,
                                        limite, offset);
    }
    
    @Transactional
    public Long registrarEvento(RegistrarEventoRequest request) {
        validarRegistrarEventoRequest(request);
        
        Long idUsuarioInclusao = AuditoriaUtil.obterIdUsuarioAutenticado();
        
        String idEventoXml = request.getIdEventoXml();
        if (idEventoXml == null || idEventoXml.isBlank()) {
            String prefixo = "ID";
            Long idPessoa = request.getIdPessoa();
            if (idPessoa != null) {
                idEventoXml = prefixo + String.format("%018d", idPessoa);
                if (idEventoXml.length() > 50) {
                    idEventoXml = idEventoXml.substring(idEventoXml.length() - 50);
                }
            } else {
                idEventoXml = prefixo + System.currentTimeMillis();
                if (idEventoXml.length() > 50) {
                    idEventoXml = idEventoXml.substring(idEventoXml.length() - 50);
                }
            }
        }
        
        return repository.registrarEvento(
            request.getIdLote(),
            request.getIdPessoa(),
            request.getIdConta(),
            request.getCpf(),
            request.getNome(),
            request.getNumeroConta(),
            request.getDigitoConta(),
            request.getSaldoAtual(),
            request.getTotCreditos(),
            request.getTotDebitos(),
            idEventoXml,
            request.getStatusEvento(),
            request.getOcorrenciasJson(),
            request.getNumeroRecibo(),
            request.getIndRetificacao(),
            idUsuarioInclusao
        );
    }
    
    @Transactional
    public List<Long> registrarEventosDoLote(RegistrarEventosRequest request) {
        if (request == null || request.getEventos() == null || request.getEventos().isEmpty()) {
            throw new IllegalArgumentException("Lista de eventos não pode estar vazia.");
        }
        
        String prefixo = (request.getIdEventoPrefix() != null && !request.getIdEventoPrefix().isBlank()) 
                        ? request.getIdEventoPrefix() 
                        : "ID";
        Integer indRetificacao = (request.getIndRetificacao() != null) ? request.getIndRetificacao() : 0;
        
        List<Long> idsEventos = new ArrayList<>();
        Long idUsuarioInclusao = AuditoriaUtil.obterIdUsuarioAutenticado();
        
        for (RegistrarEventoRequest eventoRequest : request.getEventos()) {
            validarRegistrarEventoRequest(eventoRequest);
            
            String idEventoXml = eventoRequest.getIdEventoXml();
            if (idEventoXml == null || idEventoXml.isBlank()) {
                Long idPessoa = eventoRequest.getIdPessoa();
                if (idPessoa != null) {
                    idEventoXml = prefixo + String.format("%018d", idPessoa);
                    if (idEventoXml.length() > 50) {
                        idEventoXml = idEventoXml.substring(idEventoXml.length() - 50);
                    }
                } else {
                    idEventoXml = prefixo + System.currentTimeMillis();
                    if (idEventoXml.length() > 50) {
                        idEventoXml = idEventoXml.substring(idEventoXml.length() - 50);
                    }
                }
            }
            
            Integer indRetificacaoEvento = (eventoRequest.getIndRetificacao() != null) 
                                         ? eventoRequest.getIndRetificacao() 
                                         : indRetificacao;
            
            Long idEvento = repository.registrarEvento(
                eventoRequest.getIdLote(),
                eventoRequest.getIdPessoa(),
                eventoRequest.getIdConta(),
                eventoRequest.getCpf(),
                eventoRequest.getNome(),
                eventoRequest.getNumeroConta(),
                eventoRequest.getDigitoConta(),
                eventoRequest.getSaldoAtual(),
                eventoRequest.getTotCreditos(),
                eventoRequest.getTotDebitos(),
                idEventoXml,
                eventoRequest.getStatusEvento(),
                eventoRequest.getOcorrenciasJson(),
                eventoRequest.getNumeroRecibo(),
                indRetificacaoEvento,
                idUsuarioInclusao
            );
            
            idsEventos.add(idEvento);
        }
        
        return idsEventos;
    }
    
    private void validarRegistrarEventoRequest(RegistrarEventoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo.");
        }
        if (request.getIdLote() == null || request.getIdLote() <= 0) {
            throw new IllegalArgumentException("ID do lote é obrigatório e deve ser maior que 0.");
        }
    }
}
