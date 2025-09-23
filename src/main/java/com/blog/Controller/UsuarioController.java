package com.blog.Controller;

import com.blog.Model.Usuario;
import com.blog.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> listrUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Usuario buscarUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity<?> criarUsuario(@RequestBody @Valid Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado");
        }
        Usuario salvo = usuarioRepository.save(usuario);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario novosDados) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Usuario> usuarioComMesmoEmail = usuarioRepository.findByEmail(novosDados.getEmail());
        if (usuarioComMesmoEmail.isPresent() && !usuarioComMesmoEmail.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("E-mail já está em uso");
        }
        Usuario usuario = usuarioExistente.get();
        usuario.setName(novosDados.getName());
        usuario.setEmail(novosDados.getEmail());
        usuario.setPassword(novosDados.getPassword());

        Usuario atualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
