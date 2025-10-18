package com.blog.model;

public enum Role {
    USER("Usuário comum"),
    MODERATOR("Moderador do conteúdo"),
    ADMIN("Administrador do sistema");

    private final String descricao;
    Role(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
