package br.com.bscash.efinanceira.infrastructure.util;

import br.com.bscash.efinanceira.domain.model.AuditoriaInfo;
import br.com.bscash.efinanceira.domain.model.UsuarioAutenticado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class AuditoriaUtil {
    
    private static final Long ID_USUARIO_SISTEMA = 1L;
    
    private AuditoriaUtil() {
        // Construtor privado para evitar instanciação da classe utilitária
    }
    
    public static Long obterIdUsuarioAutenticado() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("Nenhum usuário autenticado encontrado, usando usuário sistema");
                return ID_USUARIO_SISTEMA;
            }
            
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof UsuarioAutenticado usuario) {
                Long idUsuario = usuario.getId();
                log.debug("Usuário autenticado encontrado: ID {}", idUsuario);
                return idUsuario != null ? idUsuario : ID_USUARIO_SISTEMA;
            }
            
            log.debug("Principal não é UsuarioAutenticado, usando usuário sistema");
            return ID_USUARIO_SISTEMA;
        } catch (Exception e) {
            log.warn("Erro ao obter usuário autenticado: {}. Usando usuário sistema.", e.getMessage());
            return ID_USUARIO_SISTEMA;
        }
    }
    
    public static AuditoriaInfo criarAuditoriaInclusao() {
        Long idUsuario = obterIdUsuarioAutenticado();
        return AuditoriaInfo.paraInclusao(idUsuario);
    }
    
    public static AuditoriaInfo criarAuditoriaAlteracao() {
        Long idUsuario = obterIdUsuarioAutenticado();
        return AuditoriaInfo.paraAlteracao(idUsuario);
    }
    
    public static AuditoriaInfo criarAuditoriaAlteracaoSituacao(String novaSituacao) {
        Long idUsuario = obterIdUsuarioAutenticado();
        return AuditoriaInfo.paraAlteracaoSituacao(idUsuario, novaSituacao);
    }
}
