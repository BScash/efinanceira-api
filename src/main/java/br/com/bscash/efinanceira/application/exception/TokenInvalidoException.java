package br.com.bscash.efinanceira.application.exception;

public class TokenInvalidoException extends Exception {
    
    public TokenInvalidoException(String message) {
        super(message);
    }
    
    public TokenInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
