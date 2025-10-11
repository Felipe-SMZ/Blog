package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComentarioRequest {

    @NotBlank(message = "O comentário é obrigatório")
    @Size(min = 1, max = 500, message = "O conteúdo deve ter no máximo 500 caracteres")
    private String comentario;

    public ComentarioRequest() {
    }

    public ComentarioRequest(Long usuarioId, String comentario) {
        this.usuarioId = usuarioId;
        this.comentario = comentario;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getComentario() {
        return comentario;
    }
}
