package com.blog.service;

import com.blog.dto.ComentarioRequest;
import com.blog.exception.ForbiddenException;
import com.blog.model.Comentario;
import com.blog.model.Post;
import com.blog.model.Role;
import com.blog.model.Usuario;
import com.blog.repository.ComentarioRepository;
import com.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ComentarioService - Testes Unitários")
class ComentarioServiceTest {

    private ComentarioRepository comentarioRepositoryMock;
    private PostRepository postRepositoryMock;
    private ComentarioService comentarioService;

    private Usuario usuarioNormal;
    private Usuario usuarioModerador;
    private Usuario usuarioAdmin;
    private Post post;
    private Comentario comentario;

    @BeforeEach
    void setUp() {
        comentarioRepositoryMock = mock(ComentarioRepository.class);
        postRepositoryMock = mock(PostRepository.class);
        comentarioService = new ComentarioService(comentarioRepositoryMock, postRepositoryMock);

        usuarioNormal = new Usuario();
        usuarioNormal.setId(1L);
        usuarioNormal.setRole(Role.USER);

        usuarioModerador = new Usuario();
        usuarioModerador.setId(2L);
        usuarioModerador.setRole(Role.MODERATOR);

        usuarioAdmin = new Usuario();
        usuarioAdmin.setId(3L);
        usuarioAdmin.setRole(Role.ADMIN);

        post = new Post();
        post.setId(1L);
        post.setUsuario(usuarioNormal);

        comentario = new Comentario();
        comentario.setId(1L);
        comentario.setPost(post);
        comentario.setUsuario(usuarioNormal);
        comentario.setComentario("Ótimo post!");
    }

    @Test
    @DisplayName("Deve buscar comentário por ID")
    void deveBuscarComentarioPorId() {
        when(comentarioRepositoryMock.findById(1L)).thenReturn(Optional.of(comentario));

        Comentario resultado = comentarioService.buscarComentario(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("USER consegue deletar seu próprio comentário")
    void userDeletaProprioComentario() {
        when(comentarioRepositoryMock.findById(1L)).thenReturn(Optional.of(comentario));
        mockSecurityContext(usuarioNormal);

        assertDoesNotThrow(() -> comentarioService.excluirComentario(1L));
        verify(comentarioRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("USER não consegue deletar comentário alheio")
    void userNaoDeletaComentarioAlheio() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(4L);
        outroUsuario.setRole(Role.USER);

        when(comentarioRepositoryMock.findById(1L)).thenReturn(Optional.of(comentario));
        mockSecurityContext(outroUsuario);

        assertThrows(ForbiddenException.class, () -> comentarioService.excluirComentario(1L));
        verify(comentarioRepositoryMock, never()).deleteById(any());
    }

    @Test
    @DisplayName("MODERATOR consegue deletar qualquer comentário")
    void moderadorDeletaQualquerComentario() {
        when(comentarioRepositoryMock.findById(1L)).thenReturn(Optional.of(comentario));
        mockSecurityContext(usuarioModerador);

        assertDoesNotThrow(() -> comentarioService.excluirComentario(1L));
        verify(comentarioRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("ADMIN consegue deletar qualquer comentário")
    void adminDeletaQualquerComentario() {
        when(comentarioRepositoryMock.findById(1L)).thenReturn(Optional.of(comentario));
        mockSecurityContext(usuarioAdmin);

        assertDoesNotThrow(() -> comentarioService.excluirComentario(1L));
        verify(comentarioRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("USER consegue atualizar seu próprio comentário")
    void userAtualizaProprioComentario() {
        ComentarioRequest request = new ComentarioRequest("Novo comentário");
        when(comentarioRepositoryMock.findById(1L)).thenReturn(Optional.of(comentario));
        when(comentarioRepositoryMock.save(any())).thenReturn(comentario);
        mockSecurityContext(usuarioNormal);

        Comentario resultado = comentarioService.atualizarComentario(1L, request);

        assertNotNull(resultado);
        verify(comentarioRepositoryMock, times(1)).save(any());
    }

    @Test
    @DisplayName("USER não consegue atualizar comentário alheio")
    void userNaoAtualizaComentarioAlheio() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(4L);
        outroUsuario.setRole(Role.USER);

        ComentarioRequest request = new ComentarioRequest("Novo comentário");
        when(comentarioRepositoryMock.findById(1L)).thenReturn(Optional.of(comentario));
        mockSecurityContext(outroUsuario);

        assertThrows(ForbiddenException.class, () -> comentarioService.atualizarComentario(1L, request));
        verify(comentarioRepositoryMock, never()).save(any());
    }

    private void mockSecurityContext(Usuario usuario) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(usuario);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
    }
}
