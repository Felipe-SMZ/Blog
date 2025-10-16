package com.blog.controller;

import com.blog.dto.PostRequest;
import com.blog.dto.PostResponse;
import com.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> cadastrarPost(@RequestBody @Valid PostRequest postRequest) {
        return ResponseEntity.ok(postService.cadastrarPost(postRequest).toResponse());
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> listarTodosPost(Pageable pageable) {
        return ResponseEntity.ok(postService.listarTodosPosts(pageable).map(post -> post.toResponse()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> buscarPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.buscarPorId(id).toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> atualizarPost(@PathVariable Long id, @RequestBody @Valid PostRequest postRequest) {
        return ResponseEntity.ok(postService.atualizarPost(id, postRequest).toResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPost(@PathVariable Long id) {
        postService.excluirPost(id);
        return ResponseEntity.noContent().build();
    }

}
