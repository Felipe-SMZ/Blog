package com.blog.dto;

import com.blog.model.Usuario;

import java.time.LocalDateTime;

public class PostResponse {

    private Long id;
    private UsuarioResumoResponse autor;
    private String titulo;
    private String conteudo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostResponse(Long id, Usuario usuario, String titulo, String conteudo,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.autor = new UsuarioResumoResponse(usuario.getId(), usuario.getName(), usuario.getEmail());
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public UsuarioResumoResponse getAutor() {
        return autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
