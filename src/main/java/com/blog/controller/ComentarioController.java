package com.blog.controller;

import com.blog.dto.ComentarioRequest;
import com.blog.dto.ComentarioResponse;
import com.blog.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {


    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<ComentarioResponse> cadastrarComentario(
            @PathVariable Long postId,
            @RequestBody @Valid ComentarioRequest comentarioRequest) {
        return ResponseEntity.ok(comentarioService.cadastrarComentario(postId, comentarioRequest).toResponse());
    }

    @GetMapping
    public ResponseEntity<Page<ComentarioResponse>> listarTodosComentario(Pageable pageable) {
        return ResponseEntity.ok(comentarioService.listarTodosComentario(pageable).map(comentario -> comentario.toResponse())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioResponse> buscarComentario(@PathVariable Long id) {
        return ResponseEntity.ok(comentarioService.buscarComentario(id).toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponse> atualizarComentario(@PathVariable Long id, @RequestBody @Valid ComentarioRequest comentarioRequest) {
        return ResponseEntity.ok(comentarioService.atualizarComentario(id, comentarioRequest).toResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirComentario(@PathVariable Long id) {
        comentarioService.excluirComentario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<ComentarioResponse>> listarComentariosPorPost(@PathVariable Long postId, Pageable pageable) {
        return ResponseEntity.ok(comentarioService.listarComentariosPorPost(postId, pageable).map(comentario -> comentario.toResponse()));
    }
}
