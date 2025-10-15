package com.blog.handler;

import com.blog.exception.*;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manipulador global de exceções para a API do Blog.
 * Captura e formata todas as exceções em respostas JSON padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ============================================
    // EXCEÇÕES CUSTOMIZADAS (NOVAS)
    // ============================================

    /**
     * Trata ResourceNotFoundException - Quando recurso não é encontrado
     * Retorna: 404 NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.NOT_FOUND.value());
        erro.put("erro", "Recurso não encontrado");
        erro.put("mensagem", ex.getMessage());

        if (ex.getResourceName() != null) {
            erro.put("recurso", ex.getResourceName());
            erro.put("campo", ex.getFieldName());
            erro.put("valor", ex.getFieldValue());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    /**
     * Trata UnauthorizedException - Quando credenciais são inválidas
     * Retorna: 401 UNAUTHORIZED
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.UNAUTHORIZED.value());
        erro.put("erro", "Não autorizado");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    /**
     * Trata ForbiddenException - Quando usuário não tem permissão
     * Retorna: 403 FORBIDDEN
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(ForbiddenException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.FORBIDDEN.value());
        erro.put("erro", "Acesso negado");
        erro.put("mensagem", ex.getMessage());

        if (ex.getResource() != null) {
            erro.put("recurso", ex.getResource());
            erro.put("acao", ex.getAction());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    /**
     * Trata BusinessException - Quando regras de negócio são violadas
     * Retorna: 400 BAD REQUEST
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.BAD_REQUEST.value());
        erro.put("erro", "Erro de negócio");
        erro.put("mensagem", ex.getMessage());

        if (ex.getErrorCode() != null) {
            erro.put("codigo", ex.getErrorCode());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Trata InvalidTokenException - Quando token JWT é inválido
     * Retorna: 401 UNAUTHORIZED
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidToken(InvalidTokenException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.UNAUTHORIZED.value());
        erro.put("erro", "Token inválido");
        erro.put("mensagem", ex.getMessage());
        erro.put("tipoToken", ex.getTokenType());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    // ============================================
    // EXCEÇÕES EXISTENTES (MANTIDAS)
    // ============================================

    /**
     * Trata EmailDuplicadoException - Email já cadastrado
     * Retorna: 400 BAD REQUEST
     * <p>
     * NOTA: Esta exceção pode ser substituída por BusinessException no futuro
     */
    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDuplicado(EmailDuplicadoException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.BAD_REQUEST.value());
        erro.put("erro", "E-mail duplicado");
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Trata BadCredentialsException do Spring Security
     * Retorna: 401 UNAUTHORIZED
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.UNAUTHORIZED.value());
        erro.put("erro", "Credenciais inválidas");
        erro.put("mensagem", "Email ou senha incorretos");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    /**
     * Trata MalformedJwtException do JJWT
     * Retorna: 401 UNAUTHORIZED
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJwt(MalformedJwtException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.UNAUTHORIZED.value());
        erro.put("erro", "Token malformado");
        erro.put("mensagem", "Token JWT inválido");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    /**
     * Trata erros de validação do Bean Validation
     * Retorna: 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.BAD_REQUEST.value());
        erro.put("erro", "Erro de validação");

        // Coleta TODOS os erros de validação
        Map<String, String> camposComErro = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                camposComErro.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        erro.put("campos", camposComErro);

        // Mensagem principal (primeiro erro)
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Dados inválidos");
        erro.put("mensagem", mensagem);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Trata ResponseStatusException (usado internamente pelo Spring)
     * Retorna: Status definido na exceção
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", ex.getStatusCode().value());
        erro.put("erro", ex.getStatusCode().toString());
        erro.put("mensagem", ex.getReason());

        return ResponseEntity.status(ex.getStatusCode()).body(erro);
    }

    /**
     * Trata exceções genéricas não capturadas
     * Retorna: 500 INTERNAL SERVER ERROR
     * <p>
     * IMPORTANTE: Em produção, não expor detalhes do erro
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        erro.put("erro", "Erro interno do servidor");

        // Em desenvolvimento, mostra a mensagem. Em produção, ocultar!
        erro.put("mensagem", ex.getMessage());
        // erro.put("mensagem", "Ocorreu um erro inesperado. Tente novamente mais tarde."); // PRODUÇÃO

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    /**
     * Trata qualquer exceção não capturada
     * Retorna: 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("timestamp", LocalDateTime.now());
        erro.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        erro.put("erro", "Erro inesperado");
        erro.put("mensagem", "Ocorreu um erro inesperado. Entre em contato com o suporte.");

        // Log do erro (importante para debugging)
        System.err.println("Erro não tratado: " + ex.getClass().getName());
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}