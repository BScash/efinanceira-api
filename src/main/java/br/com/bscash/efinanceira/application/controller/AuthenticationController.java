package br.com.bscash.efinanceira.application.controller;

import br.com.bscash.efinanceira.domain.dto.ApiResponse;
import br.com.bscash.efinanceira.domain.dto.LoginModel;
import br.com.bscash.efinanceira.domain.dto.LoginRequest;
import br.com.bscash.efinanceira.domain.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoginModel>> autenticar(
            @RequestBody @Valid LoginRequest loginRequest,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Real-IP", required = false) String clientIp) {
        
        LoginModel loginModel = authenticationService.autenticar(loginRequest, userAgent, clientIp);
        return ResponseEntity.ok(ApiResponse.success("Autenticação realizada com sucesso", loginModel));
    }
}
