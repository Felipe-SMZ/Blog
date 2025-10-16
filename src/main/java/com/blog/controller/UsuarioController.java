package com.blog.controller;

import com.blog.dto.UsuarioRequest;
import com.blog.dto.UsuarioResponse;
import com.blog.model.Usuario;
import com.blog.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(
                usuarioService.listarTodos().stream()
                        .map(Usuario::toResponse)
                        .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id).toResponse());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@RequestBody @Valid UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.criarUsuario(request).toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarUsuario(id, request).toResponse());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getMe(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(usuarioLogado.toResponse());
    }

}

