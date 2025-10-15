package com.blog.handler;

import com.blog.exception.*;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralizador global de tratamento de exceções.
 * Todas as exceções lançadas na aplicação são capturadas aqui
 * e retornam respostas JSON formatadas com status HTTP apropriado.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ============================================================================
    // 404 - RECURSO NÃO ENCONTRADO
    // ============================================================================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.NOT_FOUND.value());
        erro.put("erro", "Recurso não encontrado");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // ============================================================================
    // 401 - NÃO AUTORIZADO (Credenciais inválidas)
    // ============================================================================
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            UnauthorizedException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.UNAUTHORIZED.value());
        erro.put("erro", "Não autorizado");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    // ============================================================================
    // 401 - TOKEN INVÁLIDO OU EXPIRADO
    // ============================================================================
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidToken(
            InvalidTokenException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.UNAUTHORIZED.value());
        erro.put("erro", "Token inválido");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    // ============================================================================
    // 403 - ACESSO NEGADO (Sem permissão para acessar recurso)
    // ============================================================================
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(
            ForbiddenException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.FORBIDDEN.value());
        erro.put("erro", "Acesso negado");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    // ============================================================================
    // 400 - ERRO DE NEGÓCIO (Violação de regras de negócio)
    // ============================================================================
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(
            BusinessException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.BAD_REQUEST.value());
        erro.put("erro", "Erro de negócio");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // ============================================================================
    // 400 - EMAIL DUPLICADO (Herança de BusinessException, mas tratado separadamente)
    // ============================================================================
    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDuplicado(
            EmailDuplicadoException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.BAD_REQUEST.value());
        erro.put("erro", "Email duplicado");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // ============================================================================
    // 401 - JWT MALFORMADO OU COM ASSINATURA INVÁLIDA
    // ============================================================================
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(
            JwtException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.UNAUTHORIZED.value());
        erro.put("erro", "Token JWT inválido");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    // ============================================================================
    // 400 - VALIDAÇÃO DE ENTRADA (Bean Validation - @Valid, @NotNull, etc)
    // ============================================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.BAD_REQUEST.value());
        erro.put("erro", "Erro de validação");

        // Extrai o primeiro erro encontrado para mensagem mais clara
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .orElse("Dados inválidos");

        erro.put("mensagem", mensagem);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // ============================================================================
    // 500 - EXCEÇÃO GENÉRICA RUNTIME (Erros não mapeados)
    // ============================================================================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        erro.put("erro", "Erro interno do servidor");
        // NÃO expor detalhes do erro para o cliente em produção
        erro.put("mensagem", "Ocorreu um erro inesperado. Tente novamente mais tarde.");

        // Log do erro real para DEBUG (em produção usar logger apropriado)
        System.err.println("[ERROR] RuntimeException:");
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    // ============================================================================
    // 500 - EXCEÇÃO GERAL (Qualquer exceção não capturada)
    // ============================================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception ex) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        erro.put("erro", "Erro interno do servidor");
        // NÃO expor detalhes do erro para o cliente
        erro.put("mensagem", "Ocorreu um erro inesperado. Tente novamente mais tarde.");

        // Log do erro real para DEBUG
        System.err.println("[ERROR] Uncaught Exception:");
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}