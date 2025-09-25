package com.blog.service;

import com.blog.dto.UsuarioRequest;
import com.blog.exception.EmailDuplicadoException;
import com.blog.model.Usuario;
import com.blog.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario criarUsuario(UsuarioRequest dto) {
        validarEmailUnico(dto.getEmail(), null);
        String senhaCriptografada = passwordEncoder.encode(dto.getPassword());
        Usuario usuario = new Usuario(dto.getName(), dto.getEmail(), senhaCriptografada);
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Long id, UsuarioRequest dto) {
        Optional<Usuario> existente = usuarioRepository.findById(id);
        if (existente.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        validarEmailUnico(dto.getEmail(), id);

        Usuario usuario = existente.get();
        usuario.setName(dto.getName());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        return usuarioRepository.save(usuario);

    }

    public void validarEmailUnico(String email, Long idAtual) {
        Optional<Usuario> existente = usuarioRepository.findByEmail(email);
        if (existente.isPresent() && !existente.get().getId().equals(idAtual)) {
            throw new EmailDuplicadoException("E-mail já está cadastrado");
        }
    }

    public void deletarUsuario(Long id) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!usuarioLogado.getId().equals(id)) {
            throw new RuntimeException("Você não pode deletar outro usuário");
        }
        usuarioRepository.deleteById(id);
    }

}
