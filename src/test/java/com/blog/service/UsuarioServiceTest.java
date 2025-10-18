package com.blog.service;

import com.blog.dto.UsuarioRequest;
import com.blog.exception.BusinessException;
import com.blog.exception.ForbiddenException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Role;
import com.blog.model.Usuario;
import com.blog.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceTest {

    private UsuarioRepository usuarioRepositoryMock;
    private PasswordEncoder passwordEncoderMock;
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioRepositoryMock = mock(UsuarioRepository.class);
        passwordEncoderMock = mock(PasswordEncoder.class);
        usuarioService = new UsuarioService(usuarioRepositoryMock, passwordEncoderMock);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setName("Felipe");
        usuario.setEmail("felipe@teste.com");
        usuario.setPassword("senha123");
        usuario.setRole(Role.USER);
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() {
        when(usuarioRepositoryMock.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void deveLancarExcecaoUsuarioInexistente() {
        when(usuarioRepositoryMock.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Deve criar usuário com dados válidos")
    void deveCriarUsuarioComDadosValidos() {
        UsuarioRequest request = new UsuarioRequest("Felipe", "felipe@email.com", "senha123");

        when(usuarioRepositoryMock.findByEmail("felipe@email.com")).thenReturn(Optional.empty());
        when(passwordEncoderMock.encode(anyString())).thenReturn("senhaEncriptada");
        when(usuarioRepositoryMock.save(any())).thenReturn(usuario);

        Usuario resultado = usuarioService.criarUsuario(request);

        assertNotNull(resultado);
        verify(usuarioRepositoryMock, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve negar criar usuário com email duplicado")
    void deveNegarCriarUsuarioEmailDuplicado() {
        UsuarioRequest request = new UsuarioRequest("Outro", "felipe@teste.com", "senha123");

        when(usuarioRepositoryMock.findByEmail("felipe@teste.com")).thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class, () -> usuarioService.criarUsuario(request));
        verify(usuarioRepositoryMock, never()).save(any());
    }

    @Test
    @DisplayName("USER só consegue deletar sua própria conta")
    void userDeletaPropriaContaApenas() {
        mockSecurityContext(usuario);

        assertDoesNotThrow(() -> usuarioService.deletarUsuario(1L));
        verify(usuarioRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("USER não consegue deletar outra conta")
    void userNaoDeletaOutraConta() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        outroUsuario.setRole(Role.USER);

        mockSecurityContext(outroUsuario);

        assertThrows(ForbiddenException.class, () -> usuarioService.deletarUsuario(1L));
        verify(usuarioRepositoryMock, never()).deleteById(any());
    }

    private void mockSecurityContext(Usuario usuario) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(usuario);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
    }
}