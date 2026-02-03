package br.com.bscash.efinanceira.application.exception;

public class AutenticacaoException extends RuntimeException {
    
    public AutenticacaoException(String message) {
        super(message);
    }
    
    public AutenticacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
