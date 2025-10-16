package com.blog.controller;

import com.blog.dto.PostRequest;
import com.blog.dto.PostResponse;
import com.blog.model.Post;
import com.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> cadastrarPost(@RequestBody @Valid PostRequest postRequest) {
        Post post = postService.cadastrarPost(postRequest);
        PostResponse resposta = new PostResponse(
                post.getId(),
                post.getUsuario(),
                post.getTitulo(),
                post.getConteudo(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
        return ResponseEntity.ok(resposta);


    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> listarTodosPost() {
        List<Post> posts = postService.listarTodosPosts();
        List<PostResponse> resposta = posts.stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getUsuario(),
                        post.getTitulo(),
                        post.getConteudo(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                ))
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> buscarPost(@PathVariable Long id) {
        Post post = postService.buscarPorId(id);
        PostResponse resposta = new PostResponse(
                post.getId(),
                post.getUsuario(),
                post.getTitulo(),
                post.getConteudo(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> atualizarPost(@PathVariable Long id, @RequestBody @Valid PostRequest postRequest) {
        Post post = postService.atualizarPost(id, postRequest);
        PostResponse resposta = new PostResponse(
                post.getId(),
                post.getUsuario(),
                post.getTitulo(),
                post.getConteudo(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPost(@PathVariable Long id) {
        postService.excluirPost(id);
        return ResponseEntity.noContent().build();
    }

}
