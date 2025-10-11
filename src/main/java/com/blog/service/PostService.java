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

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post não encontrado"
                ));

        // ✅ VERIFICA SE É O DONO DO POST
        if (!post.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode editar este post"
            );
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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post não encontrado"
                ));

        if (!post.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode excluir este post"
            );
        }

        postRepository.deleteById(id);
    }


    public List<Post> listarTodosPosts() {
        return postRepository.findAll();
    }

    public Post buscarPorId(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post não encontrado"
                ));
    }
}
