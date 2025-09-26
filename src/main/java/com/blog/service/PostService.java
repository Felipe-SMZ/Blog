package com.blog.service;

import com.blog.dto.PostRequest;
import com.blog.model.Post;
import com.blog.model.Usuario;
import com.blog.repository.PostRepository;
import com.blog.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Post cadastrarPost(PostRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Post post = new Post(usuario, request.getTitulo(), request.getConteudo(), null, null);
        return postRepository.save(post);
    }

    public Post atualizarPost(Long id, PostRequest request) {
        Optional<Post> existente = postRepository.findById(id);
        if (existente.isEmpty()) {
            throw new RuntimeException("Post não encontrado");
        }
        Post post = existente.get();
        post.setTitulo(request.getTitulo());
        post.setConteudo(request.getConteudo());

        return postRepository.save(post);
    }

    public void excluirPost(Long id) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        if (!post.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você nçao pode excluir este post");
        }

        postRepository.deleteById(id);
    }

    public List<Post> listarTodosPosts() {
        return postRepository.findAll();
    }

    public Post buscarPost(Long id) {
        Optional<Post> post = Optional.ofNullable(postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado")));
        return post.get();
    }

    public Post buscarPorId(Long id) {
        return postRepository.findById(id).orElseThrow(()-> new RuntimeException("Post não encontrado"));
    }
}
