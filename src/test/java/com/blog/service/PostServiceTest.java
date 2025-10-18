package com.blog.service;

import com.blog.dto.PostRequest;
import com.blog.exception.ForbiddenException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Post;
import com.blog.model.Role;
import com.blog.model.Usuario;
import com.blog.repository.PostRepository;
import com.blog.repository.UsuarioRepository;
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

@DisplayName("PostService - Testes Unitários")
class PostServiceTest {

    private PostRepository postRepositoryMock;
    private UsuarioRepository usuarioRepositoryMock;
    private PostService postService;

    private Usuario usuarioNormal;
    private Usuario usuarioAdmin;
    private Post post;

    @BeforeEach
    void setUp() {
        postRepositoryMock = mock(PostRepository.class);
        usuarioRepositoryMock = mock(UsuarioRepository.class);
        postService = new PostService(postRepositoryMock, usuarioRepositoryMock);

        usuarioNormal = new Usuario();
        usuarioNormal.setId(1L);
        usuarioNormal.setRole(Role.USER);
        usuarioNormal.setName("Felipe");

        usuarioAdmin = new Usuario();
        usuarioAdmin.setId(2L);
        usuarioAdmin.setRole(Role.ADMIN);
        usuarioAdmin.setName("Admin");

        post = new Post();
        post.setId(1L);
        post.setUsuario(usuarioNormal);
        post.setTitulo("Meu Post");
        post.setConteudo("Conteúdo");
    }

    @Test
    @DisplayName("Deve buscar post por ID")
    void deveBuscarPostPorId() {
        when(postRepositoryMock.findById(1L)).thenReturn(Optional.of(post));

        Post resultado = postService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(postRepositoryMock, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar post inexistente")
    void deveLancarExcecaoPostInexistente() {
        when(postRepositoryMock.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.buscarPorId(999L));
    }

    @Test
    @DisplayName("USER consegue deletar seu próprio post")
    void userDeletaProprioPost() {
        when(postRepositoryMock.findById(1L)).thenReturn(Optional.of(post));
        mockSecurityContext(usuarioNormal);

        assertDoesNotThrow(() -> postService.excluirPost(1L));
        verify(postRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("USER não consegue deletar post alheio")
    void userNaoDeletaPostAlheio() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(3L);
        outroUsuario.setRole(Role.USER);

        when(postRepositoryMock.findById(1L)).thenReturn(Optional.of(post));
        mockSecurityContext(outroUsuario);

        assertThrows(ForbiddenException.class, () -> postService.excluirPost(1L));
        verify(postRepositoryMock, never()).deleteById(any());
    }

    @Test
    @DisplayName("ADMIN consegue deletar qualquer post")
    void adminDeletaQualquerPost() {
        when(postRepositoryMock.findById(1L)).thenReturn(Optional.of(post));
        mockSecurityContext(usuarioAdmin);

        assertDoesNotThrow(() -> postService.excluirPost(1L));
        verify(postRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("USER consegue atualizar seu próprio post")
    void userAtualizaProprioPost() {
        PostRequest request = new PostRequest("Novo Título", "Novo Conteúdo");
        when(postRepositoryMock.findById(1L)).thenReturn(Optional.of(post));
        when(postRepositoryMock.save(any())).thenReturn(post);
        mockSecurityContext(usuarioNormal);

        Post resultado = postService.atualizarPost(1L, request);

        assertNotNull(resultado);
        verify(postRepositoryMock, times(1)).save(any());
    }

    @Test
    @DisplayName("USER não consegue atualizar post alheio")
    void userNaoAtualizaPostAlheio() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(3L);
        outroUsuario.setRole(Role.USER);

        PostRequest request = new PostRequest("Novo Título", "Novo Conteúdo");
        when(postRepositoryMock.findById(1L)).thenReturn(Optional.of(post));
        mockSecurityContext(outroUsuario);

        assertThrows(ForbiddenException.class, () -> postService.atualizarPost(1L, request));
        verify(postRepositoryMock, never()).save(any());
    }

    private void mockSecurityContext(Usuario usuario) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(usuario);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
    }
}