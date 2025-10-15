package com.blog.service;

import com.blog.dto.UsuarioRequest;
import com.blog.exception.BusinessException;
import com.blog.exception.ForbiddenException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Usuario;
import com.blog.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
    }

    public Usuario criarUsuario(UsuarioRequest dto) {
        validarEmailUnico(dto.getEmail(), null);

        String senhaCriptografada = passwordEncoder.encode(dto.getPassword());
        Usuario usuario = new Usuario(dto.getName(), dto.getEmail(), senhaCriptografada);

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Long id, UsuarioRequest dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));

        validarEmailUnico(dto.getEmail(), id);

        usuario.setName(dto.getName());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        return usuarioRepository.save(usuario);
    }

    public void validarEmailUnico(String email, Long idAtual) {
        usuarioRepository.findByEmail(email).ifPresent(existente -> {
            if (!existente.getId().equals(idAtual)) {
                throw new BusinessException("E-mail já está cadastrado");
            }
        });
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));
    }

    public void deletarUsuario(Long id) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!usuarioLogado.getId().equals(id)) {
            throw new ForbiddenException("Você não pode deletar outro usuário");
        }

        usuarioRepository.deleteById(id);
    }
}