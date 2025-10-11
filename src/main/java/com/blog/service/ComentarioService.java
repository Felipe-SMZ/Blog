package com.blog.service;

import com.blog.dto.ComentarioRequest;
import com.blog.model.Comentario;
import com.blog.model.Post;
import com.blog.model.Usuario;
import com.blog.repository.ComentarioRepository;
import com.blog.repository.PostRepository;
import com.blog.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Comentario cadastrarComentario(Long postId, ComentarioRequest comentarioRequest) {
        // Pega usuário autenticado
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));

        Comentario comentario = new Comentario(
                post,
                comentarioRequest.getComentario(),
                usuarioLogado
        );
        return comentarioRepository.save(comentario);
    }

    public List<Comentario> listarTodosComentario() {
        return comentarioRepository.findAll();
    }

    public Comentario buscarComentario(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));
    }

    public Comentario atualizarComentario(Long id, ComentarioRequest comentarioRequest) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        // ✅ VERIFICA SE É O DONO DO COMENTÁRIO
        if (!comentario.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode editar este comentário"
            );
        }

        comentario.setComentario(comentarioRequest.getComentario());
        return comentarioRepository.save(comentario);
    }

    public void excluirComentario(Long id) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        Usuario usuarioLogado = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Comentario comentario = buscarComentario(id);
        if (!comentario.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode excluir este comentário");
        }
        comentarioRepository.deleteById(id);
    }
}
