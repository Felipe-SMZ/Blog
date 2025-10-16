package com.blog.repository;

import com.blog.model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    Page<Comentario> findByUsuarioId(Long usuarioId, Pageable pageable);
    Page<Comentario> findByPostId(Long postId, Pageable pageable);
}
