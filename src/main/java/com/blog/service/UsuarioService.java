package com.blog.service;

import com.blog.model.Usuario;
import com.blog.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioRepository criarUsuario(Usuario usuario) {
        validarEmailUnico(usuario.getEmail(), null);
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Long id, Usuario novosDados) {
        Optional<Usuario> existente = usuarioRepository.findById(id);
        if (existente.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado.")
        }
        validarEmailUnico(novosDados.getEmail(), id);
        Usuario usuario = existente.get();
        usuario.setName(novosDados.getName());
        usuario.setEmail(novosDados.getEmail());
        usuario.setPassword(novosDados.getPassword());
        return usuarioRepository.save(usuario);

    }

    public void validarEmailUnico(String email, Long idAtual) {
        Optional<Usuario> existente = usuarioRepository.findByEmail(email);
        if (existente.isPresent() && !existente.get().getId().equals(idAtual)) {
            throw new EmailDuplicadoExeption("E-mail já está cadastrado");
        }
    }

}
