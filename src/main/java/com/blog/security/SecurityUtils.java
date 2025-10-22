package com.blog.security;

import com.blog.exception.ForbiddenException;
import com.blog.model.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utilitário centralizado para operações de segurança.
 * Evita repetição de código nos Services.
 */
@Component
public class SecurityUtils {

    /**
     * Obtém o usuário autenticado do contexto de segurança
     */
    public Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    /**
     * Verifica se o usuário logado é o dono do recurso
     * Lança ForbiddenException se não for
     */
    public void verificarProprietario(Long autorId, String recurso, String acao) {
        Usuario usuarioLogado = getUsuarioLogado();

        if (!usuarioLogado.getId().equals(autorId)) {
            throw new ForbiddenException(recurso, acao);
        }
    }

    /**
     * Verifica se é o dono OU é admin
     */
    public void verificarProprietarioOuAdmin(Long autorId, String recurso, String acao) {
        Usuario usuarioLogado = getUsuarioLogado();

        // Admin pode tudo
        if (usuarioLogado.isAdmin()) {
            return;
        }

        // Se não é admin, precisa ser o dono
        if (!usuarioLogado.getId().equals(autorId)) {
            throw new ForbiddenException(recurso, acao);
        }
    }

    /**
     * Verifica se é o dono OU é admin/moderador
     */
    public void verificarProprietarioOuModerador(Long autorId, String recurso, String acao) {
        Usuario usuarioLogado = getUsuarioLogado();

        // Admin ou Moderador podem
        if (usuarioLogado.isAdmin() || usuarioLogado.isModerator()) {
            return;
        }

        // Se não, precisa ser o dono
        if (!usuarioLogado.getId().equals(autorId)) {
            throw new ForbiddenException(recurso, acao);
        }
    }
}