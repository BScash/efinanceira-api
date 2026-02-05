package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.application.exception.AutenticacaoException;
import br.com.bscash.efinanceira.domain.dto.AccessTokenModel;
import br.com.bscash.efinanceira.domain.dto.LoginModel;
import br.com.bscash.efinanceira.domain.dto.LoginRequest;
import br.com.bscash.efinanceira.infrastructure.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationService(UsuarioRepository usuarioRepository, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public LoginModel autenticar(LoginRequest loginRequest) {
        log.debug("Iniciando autenticação para login: {}", loginRequest.getLogin());
        
        var usuarioOpt = usuarioRepository.buscarPorLoginOuCpf(loginRequest.getLogin());
        
        if (usuarioOpt.isEmpty()) {
            log.warn("Usuário não encontrado: {}", loginRequest.getLogin());
            throw new AutenticacaoException("Credenciais inválidas");
        }
        
        var usuario = usuarioOpt.get();
        
        if (!usuario.isAtivo()) {
            log.warn("Tentativa de login com usuário inativo: {}", loginRequest.getLogin());
            throw new AutenticacaoException("Usuário inativo");
        }
        
        if (usuario.isBloqueado()) {
            log.warn("Tentativa de login com usuário bloqueado: {}", loginRequest.getLogin());
            throw new AutenticacaoException("Usuário bloqueado");
        }
        
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            log.warn("Tentativa de login com usuário sem senha cadastrada: {}", loginRequest.getLogin());
            throw new AutenticacaoException("Senha não cadastrada");
        }
        
        boolean senhaValida = passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha());
        
        if (!senhaValida) {
            log.warn("Senha inválida para usuário: {}", loginRequest.getLogin());
            throw new AutenticacaoException("Credenciais inválidas");
        }
        
        log.debug("Autenticação bem-sucedida para usuário ID: {}", usuario.getId());
        
        String accessToken = tokenService.gerarToken(usuario.getId());
        String refreshToken = tokenService.gerarRefreshToken();
        
        return new LoginModel(refreshToken, new AccessTokenModel(accessToken, "Bearer"));
    }
}
