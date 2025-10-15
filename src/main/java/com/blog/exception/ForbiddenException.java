package com.blog.exception;

/**
 * Exceção lançada quando usuário tenta acessar/modificar recurso sem permissão.
 * Exemplos: Editar post de outro usuário, deletar comentário alheio.
 * HTTP Status: 403 FORBIDDEN
 */
public class ForbiddenException extends RuntimeException {

    private final String resource;
    private final String action;

    public ForbiddenException(String resource, String action) {
        super(String.format("Você não tem permissão para %s este(a) %s", action, resource));
        this.resource = resource;
        this.action = action;
    }

    public ForbiddenException(String message) {
        super(message);
        this.resource = null;
        this.action = null;
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }
}
