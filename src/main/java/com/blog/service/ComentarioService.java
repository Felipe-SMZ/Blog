package com.blog.service;

import com.blog.dto.ComentarioRequest;
import com.blog.exception.ForbiddenException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Comentario;
import com.blog.model.Post;
import com.blog.model.Usuario;
import com.blog.repository.ComentarioRepository;
import com.blog.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    private final PostRepository postRepository;

    public ComentarioService(
            ComentarioRepository comentarioRepository,
            PostRepository postRepository
    ) {
        this.comentarioRepository = comentarioRepository;
        this.postRepository = postRepository;
    }

    public Comentario cadastrarComentario(Long postId, ComentarioRequest comentarioRequest) {
        // Pega usuário autenticado
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comentario comentario = new Comentario(
                post,
                comentarioRequest.getComentario(),
                usuarioLogado
        );
        return comentarioRepository.save(comentario);
    }

    public Page<Comentario> listarTodosComentario(Pageable pageable) {
        return comentarioRepository.findAll(pageable);
    }

    public Comentario buscarComentario(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário", "id", id));
    }

    public Comentario atualizarComentario(Long id, ComentarioRequest comentarioRequest) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário", "id", id));

        // Verifica se é o dono do comentário
        if (!comentario.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ForbiddenException("comentário", "editar");
        }

        comentario.setComentario(comentarioRequest.getComentario());
        return comentarioRepository.save(comentario);
    }

    public void excluirComentario(Long id) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Comentario comentario = buscarComentario(id);

        if (!comentario.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ForbiddenException("comentário", "deletar");
        }

        comentarioRepository.deleteById(id);
    }
}