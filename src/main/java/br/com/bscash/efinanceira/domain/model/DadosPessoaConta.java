package br.com.bscash.efinanceira.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosPessoaConta {
    private Long idPessoa;
    private String documento;
    private String nome;
    private String cpf;
    private String nacionalidade;
    private String telefone;
    private String email;
    private Long idConta;
    private String numeroConta;
    private String digitoConta;
    private BigDecimal saldoAtual;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String tipoLogradouro;
    private String enderecoLivre;
    private BigDecimal totCreditos;
    private BigDecimal totDebitos;
}
