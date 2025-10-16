package com.blog.service;

import com.blog.dto.PostRequest;
import com.blog.exception.ForbiddenException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Post;
import com.blog.model.Usuario;
import com.blog.repository.PostRepository;
import com.blog.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {


    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;

    public PostService(
            PostRepository postRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.postRepository = postRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<Post> listarTodosPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post cadastrarPost(PostRequest request) {
        // Pega o usuário autenticado do contexto de segurança
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Post post = new Post(
                usuarioLogado,
                request.getTitulo(),
                request.getConteudo(),
                null,
                null
        );
        return postRepository.save(post);
    }

    public Post atualizarPost(Long id, PostRequest request) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        // Verifica se é o dono do post
        if (!post.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ForbiddenException("post", "editar");
        }

        post.setTitulo(request.getTitulo());
        post.setConteudo(request.getConteudo());
        return postRepository.save(post);
    }

    public void excluirPost(Long id) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        if (!post.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ForbiddenException("post", "deletar");
        }

        postRepository.deleteById(id);
    }

    public List<Post> listarTodosPosts() {
        return postRepository.findAll();
    }

    public Post buscarPorId(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
    }

    // metodos busca por titulo
    public Page<Post> buscarPorTitulo(String titulo, Pageable pageable) {
        return postRepository.findByTituloContainingIgnoreCase(titulo, pageable);
    }

    // metodos busca por conteudo
    public Page<Post> buscarPorConteudo(String conteudo, Pageable pageable) {
        return postRepository.findByConteudoContainingIgnoreCase(conteudo, pageable);
    }

    // filtrar por usuario
    public Page<Post> buscarPorUsuario(Long usuarioId, Pageable pageable) {
        return postRepository.findByUsuarioId(usuarioId, pageable);
    }
}