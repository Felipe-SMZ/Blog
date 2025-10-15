package com.blog.exception;

/**
 * Exceção lançada quando token JWT é inválido ou expirado.
 * Exemplos: Token malformado, assinatura inválida, token expirado.
 * HTTP Status: 401 UNAUTHORIZED
 */
public class InvalidTokenException extends RuntimeException {

    private final String tokenType;

    public InvalidTokenException(String message) {
        super(message);
        this.tokenType = "JWT";
    }

    public InvalidTokenException(String message, String tokenType) {
        super(message);
        this.tokenType = tokenType;
    }

    public InvalidTokenException() {
        super("Token inválido ou expirado. Faça login novamente.");
        this.tokenType = "JWT";
    }

    public String getTokenType() {
        return tokenType;
    }
}
