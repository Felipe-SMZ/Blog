package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequest {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 50, message = "O título deve ter no máximo 50 caracteres")
    private String titulo;

    @NotBlank(message = "O conteúdo é obrigatório")
    @Size(max = 500, message = "O conteúdo deve ter no máximo 500 caracteres")
    private String conteudo;

    public PostRequest() {
    }

    public PostRequest(String titulo, String conteudo) {
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }
}
