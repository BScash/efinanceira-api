package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import br.com.bscash.efinanceira.domain.model.LoteBancoInfo;
import br.com.bscash.efinanceira.infrastructure.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoteService {
    
    private final LoteRepository repository;
    
    public List<LoteBancoInfo> buscarLotes(LocalDateTime dataInicio, LocalDateTime dataFim, 
                                          String periodo, String ambiente, Integer limite) {
        DatasAjustadas datas = ajustarDatas(dataInicio, dataFim);
        return repository.buscarLotes(datas.dataInicio(), datas.dataFim(), periodo, ambiente, limite);
    }
    
    public LoteBancoInfo buscarLotePorProtocolo(String protocolo) {
        if (protocolo == null || protocolo.isBlank()) {
            throw new IllegalArgumentException("Protocolo é obrigatório.");
        }
        return repository.buscarLotePorProtocolo(protocolo);
    }
    
    public List<EventoBancoInfo> buscarEventosDoLote(Long idLote) {
        if (idLote == null || idLote <= 0) {
            throw new IllegalArgumentException("ID do lote é obrigatório e deve ser maior que 0.");
        }
        return repository.buscarEventosDoLote(idLote);
    }
    
    public boolean verificarAberturaEnviadaParaPeriodo(String periodo, String ambiente) {
        if (periodo == null || periodo.isBlank()) {
            throw new IllegalArgumentException("Período é obrigatório.");
        }
        if (ambiente == null || ambiente.isBlank()) {
            throw new IllegalArgumentException("Ambiente é obrigatório.");
        }
        return repository.verificarAberturaEnviadaParaPeriodo(periodo, ambiente);
    }
    
    private DatasAjustadas ajustarDatas(LocalDateTime dataInicio, LocalDateTime dataFim) {
        LocalDateTime dataInicioAjustada = dataInicio;
        LocalDateTime dataFimAjustada = dataFim;
        
        if (dataInicio != null && dataFim == null) {
            dataFimAjustada = dataInicio.with(LocalTime.MAX);
        } else if (dataInicio == null && dataFim != null) {
            dataInicioAjustada = dataFim.with(LocalTime.MIN);
        }
        
        return new DatasAjustadas(dataInicioAjustada, dataFimAjustada);
    }
    
    private record DatasAjustadas(LocalDateTime dataInicio, LocalDateTime dataFim) {}
}
