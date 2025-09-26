package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostRequest {
    @NotNull
    private Long usuarioId;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 50, message = "O título deve ter no máximo 50 caracteres")
    private String titulo;

    @NotBlank(message = "O conteúdo é obrigatório")
    @Size(max = 500, message = "O conteúdo deve ter no máximo 500 caracteres")
    private String conteudo;

    public PostRequest() {
    }

    public PostRequest(Long usuarioId, String titulo, String conteudo) {
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }
}
