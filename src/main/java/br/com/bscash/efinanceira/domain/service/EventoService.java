package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.domain.dto.AtualizarEventoRequest;
import br.com.bscash.efinanceira.domain.dto.EventosRequest;
import br.com.bscash.efinanceira.domain.dto.RegistrarEventoRequest;
import br.com.bscash.efinanceira.domain.dto.RegistrarEventosRequest;
import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import br.com.bscash.efinanceira.infrastructure.repository.EventoRepository;
import br.com.bscash.efinanceira.infrastructure.util.AuditoriaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {
    
    private final EventoRepository repository;
    
    @Transactional(readOnly = true)
    public List<EventoBancoInfo> buscarEventos(EventosRequest request) {
        if (request == null) {
            request = EventosRequest.builder().build();
        }
        
        Integer limite = request.getLimite() != null ? request.getLimite() : 100;
        Integer offset = request.getOffset() != null ? request.getOffset() : 0;
        
        return repository.buscarEventos(
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
            limite,
            offset
        );
    }
    
    @Transactional
    public Long registrarEvento(RegistrarEventoRequest request) {
        validarRegistrarEventoRequest(request);
        
        Long idUsuarioInclusao = AuditoriaUtil.obterIdUsuarioAutenticado();
        String idEventoXml = gerarIdEventoXml(request.getIdEventoXml(), request.getIdPessoa(), "ID");
        
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
        validarRegistrarEventosRequest(request);
        
        String prefixo = obterPrefixoIdEvento(request.getIdEventoPrefix());
        Integer indRetificacaoPadrao = obterIndRetificacaoPadrao(request.getIndRetificacao());
        Long idUsuarioInclusao = AuditoriaUtil.obterIdUsuarioAutenticado();
        
        List<Long> idsEventos = new ArrayList<>();
        for (RegistrarEventoRequest eventoRequest : request.getEventos()) {
            Long idEvento = registrarEventoDoLote(eventoRequest, prefixo, indRetificacaoPadrao, idUsuarioInclusao);
            idsEventos.add(idEvento);
        }
        
        return idsEventos;
    }
    
    private void validarRegistrarEventosRequest(RegistrarEventosRequest request) {
        if (request == null || request.getEventos() == null || request.getEventos().isEmpty()) {
            throw new IllegalArgumentException("Lista de eventos não pode estar vazia.");
        }
    }
    
    private String obterPrefixoIdEvento(String prefixo) {
        return (prefixo != null && !prefixo.isBlank()) ? prefixo : "ID";
    }
    
    private Integer obterIndRetificacaoPadrao(Integer indRetificacao) {
        return (indRetificacao != null) ? indRetificacao : 0;
    }
    
    private Long registrarEventoDoLote(RegistrarEventoRequest eventoRequest, String prefixo, 
                                       Integer indRetificacaoPadrao, Long idUsuarioInclusao) {
        validarRegistrarEventoRequest(eventoRequest);
        
        String idEventoXml = gerarIdEventoXml(eventoRequest.getIdEventoXml(), eventoRequest.getIdPessoa(), prefixo);
        Integer indRetificacaoEvento = obterIndRetificacaoEvento(eventoRequest.getIndRetificacao(), indRetificacaoPadrao);
        
        return repository.registrarEvento(
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
    }
    
    private String gerarIdEventoXml(String idEventoXmlExistente, Long idPessoa, String prefixo) {
        if (idEventoXmlExistente != null && !idEventoXmlExistente.isBlank()) {
            return idEventoXmlExistente;
        }
        
        String idEventoXml;
        if (idPessoa != null) {
            idEventoXml = prefixo + String.format("%018d", idPessoa);
        } else {
            idEventoXml = prefixo + System.currentTimeMillis();
        }
        
        return truncarIdEventoXml(idEventoXml);
    }
    
    private String truncarIdEventoXml(String idEventoXml) {
        if (idEventoXml.length() > 50) {
            return idEventoXml.substring(idEventoXml.length() - 50);
        }
        return idEventoXml;
    }
    
    private Integer obterIndRetificacaoEvento(Integer indRetificacaoEvento, Integer indRetificacaoPadrao) {
        return (indRetificacaoEvento != null) ? indRetificacaoEvento : indRetificacaoPadrao;
    }
    
    @Transactional
    public void atualizarEvento(Long idEvento, AtualizarEventoRequest request) {
        if (idEvento == null || idEvento <= 0) {
            throw new IllegalArgumentException("ID do evento é obrigatório e deve ser maior que 0.");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo.");
        }
        
        Long idUsuarioAlteracao = AuditoriaUtil.obterIdUsuarioAutenticado();
        
        repository.atualizarEvento(
            idEvento,
            request.getStatusEvento(),
            request.getOcorrenciasJson(),
            request.getNumeroRecibo(),
            idUsuarioAlteracao
        );
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
