package com.blog.controller;

import com.blog.dto.ComentarioRequest;
import com.blog.dto.ComentarioResponse;
import com.blog.dto.UsuarioResumoResponse;
import com.blog.model.Comentario;
import com.blog.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<ComentarioResponse> cadastrarComentario(
            @PathVariable Long postId,
            @RequestBody @Valid ComentarioRequest comentarioRequest) {

        Comentario comentario = comentarioService.cadastrarComentario(postId, comentarioRequest);
        ComentarioResponse response = new ComentarioResponse(
                comentario.getId(),
                new UsuarioResumoResponse(
                        comentario.getUsuario().getId(),
                        comentario.getUsuario().getName(),
                        comentario.getUsuario().getEmail()
                ),
                comentario.getComentario(),
                comentario.getCreatedAt(),
                comentario.getUpdatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ComentarioResponse>> listarTodosComentario() {
        List<Comentario> comentarios = comentarioService.listarTodosComentario();
        List<ComentarioResponse> response = comentarios.stream()
                .map(comentario -> new ComentarioResponse(
                        comentario.getId(),
                        new UsuarioResumoResponse(
                                comentario.getUsuario().getId(),
                                comentario.getUsuario().getName(),
                                comentario.getUsuario().getEmail()
                        ),
                        comentario.getComentario(),
                        comentario.getCreatedAt(),
                        comentario.getUpdatedAt()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioResponse> buscarComentario(@PathVariable Long id) {
        Comentario comentario = comentarioService.buscarComentario(id);
        ComentarioResponse response = new ComentarioResponse(
                comentario.getId(),
                new UsuarioResumoResponse(
                        comentario.getUsuario().getId(),
                        comentario.getUsuario().getName(),
                        comentario.getUsuario().getEmail()
                ),
                comentario.getComentario(),
                comentario.getCreatedAt(),
                comentario.getUpdatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponse> atualizarComentario(@PathVariable Long id, @RequestBody @Valid ComentarioRequest comentarioRequest) {
        Comentario comentario = comentarioService.atualizarComentario(id, comentarioRequest);
        ComentarioResponse response = new ComentarioResponse(
                comentario.getId(),
                new UsuarioResumoResponse(
                        comentario.getUsuario().getId(),
                        comentario.getUsuario().getName(),
                        comentario.getUsuario().getEmail()
                ),
                comentario.getComentario(),
                comentario.getCreatedAt(),
                comentario.getUpdatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirComentario(@PathVariable Long id) {
        comentarioService.excluirComentario(id);
        return ResponseEntity.noContent().build();
    }
}
