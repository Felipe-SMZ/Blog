package com.blog.controller;

import com.blog.dto.UsuarioRequest;
import com.blog.dto.UsuarioResponse;
import com.blog.model.Usuario;
import com.blog.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<UsuarioResponse> resposta = usuarios.stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getName(), u.getEmail()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        UsuarioResponse resposta = new UsuarioResponse(usuario.getId(), usuario.getName(), usuario.getEmail());
        return ResponseEntity.ok(resposta);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@RequestBody @Valid UsuarioRequest request) {
        Usuario usuario = usuarioService.criarUsuario(request);
        UsuarioResponse resposta = new UsuarioResponse(usuario.getId(), usuario.getName(), usuario.getEmail());
        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        Usuario usuario = usuaarioService.atualizarUsuario(id, request);
        UsuarioResponse resposta = new UsuarioResponse(usuario.getId(), usuario.getName(), usuario.getEmail());
        return ResponseEntity.ok(resposta);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}

