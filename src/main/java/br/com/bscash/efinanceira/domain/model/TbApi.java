package br.com.bscash.efinanceira.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbApi {
    private Long idapi;
    private String apikey;
    private LocalDateTime dataalteracao;
    private LocalDateTime dataalteracaosituacao;
    private LocalDateTime datainclusao;
    private String situacao;
    private String baseuri;
    private String baseuriauthentication;
    private String clientid;
    private LocalDateTime dataexpiracao;
    private String documentacao;
    private String nome;
    private String refreshtoken;
    private String secretkey;
    private String tokenapi;
    private String tokencallback;
    private String tokentype;
    private String urlcallback;
    private String userid;
    private Long idusuarioalteracaosituacao;
    private Long idusuarioalteracao;
    private Long idusuarioinclusao;
    private String chaveprivada;
}
