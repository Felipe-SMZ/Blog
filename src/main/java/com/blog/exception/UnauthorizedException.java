package com.blog.exception;

/**
 * Exceção lançada quando as credenciais são inválidas.
 * Exemplos: Login com senha incorreta, email não existe.
 * HTTP Status: 401 UNAUTHORIZED
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Credenciais inválidas. Verifique seu email e senha.");
    }
}
