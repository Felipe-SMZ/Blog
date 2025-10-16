package com.blog.repository;

import com.blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    //Paginacao
    Page<Post> findAll(Pageable pageable);

    // Buscar post por usuario
    Page<Post> findByUsuarioId(Long usuarioId, Pageable pageable);

    // Buscar post por titulo
    Page<Post> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    // Buscar post por conteudo
    Page<Post> findByConteudoContainingIgnoreCase(String conteudo, Pageable pageable);

}
