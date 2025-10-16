package com.blog.model;

import com.blog.dto.ComentarioResponse;
import com.blog.dto.UsuarioResumoResponse;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false, length = 500)
    private String comentario;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public Comentario() {
    }

    public Comentario(Post post, String comentario, Usuario usuario) {
        this.post = post;
        this.comentario = comentario;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Converte para ComentarioResponse
    public ComentarioResponse toResponse() {
        return new ComentarioResponse(
                this.getId(),
                new UsuarioResumoResponse(
                        this.getUsuario().getId(),
                        this.getUsuario().getName(),
                        this.getUsuario().getEmail()
                ),
                this.getComentario(),
                this.getCreatedAt(),
                this.getUpdatedAt()
        );
    }
}
