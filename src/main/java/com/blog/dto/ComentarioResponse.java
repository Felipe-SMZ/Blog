package com.blog.dto;

import java.time.LocalDateTime;

public class ComentarioResponse {

    private Long id;
    private UsuarioResumoResponse autor;
    private String comentario;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComentarioResponse(Long id,
                              UsuarioResumoResponse autor,
                              String comentario,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
        this.id = id;
        this.autor = autor;
        this.comentario = comentario;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public UsuarioResumoResponse getAutor() {
        return autor;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
