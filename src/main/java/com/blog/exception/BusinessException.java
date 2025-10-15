package com.blog.exception;

/**
 * Exceção lançada quando regras de negócio são violadas.
 * Exemplos: Email duplicado, dados inválidos, limites excedidos.
 * HTTP Status: 400 BAD REQUEST
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = null;
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}