package com.blog.controller;

import com.blog.model.Role;
import com.blog.model.Usuario;
import com.blog.repository.PostRepository;
import com.blog.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthController - Testes de Integração")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuarioTeste;
    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuarioTeste = new Usuario();
        usuarioTeste.setName("Felipe Test");
        usuarioTeste.setEmail("teste@email.com");
        usuarioTeste.setPassword(passwordEncoder.encode("senha123"));
        usuarioTeste.setRole(Role.USER);
        usuarioRepository.save(usuarioTeste);
    }

    @Test
    @DisplayName("Deve fazer login com credenciais válidas")
    void deveLogarComCredenciaisValidas() throws Exception {
        String json = """
                    {
                        "email": "teste@email.com",
                        "password": "senha123"
                    }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Deve negar login com email inválido")
    void deveNegarLoginEmailInvalido() throws Exception {
        String json = """
                    {
                        "email": "naoexit@email.com",
                        "password": "senha123"
                    }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Não autorizado"));
    }

    @Test
    @DisplayName("Deve negar login com senha inválida")
    void deveNegarLoginSenhaInvalida() throws Exception {
        String json = """
                    {
                        "email": "teste@email.com",
                        "password": "senhaErrada"
                    }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Não autorizado"));
    }

    @Test
    @DisplayName("Deve cadastrar novo usuário")
    void deveCadastrarNovoUsuario() throws Exception {
        String json = """
                    {
                        "name": "Novo User",
                        "email": "novo@email.com",
                        "password": "senha456"
                    }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Novo User"))
                .andExpect(jsonPath("$.email").value("novo@email.com"));
    }

    @Test
    @DisplayName("Deve negar cadastro com email duplicado")
    void deveNegarCadastroEmailDuplicado() throws Exception {
        String json = """
                    {
                        "name": "Outro Felipe",
                        "email": "teste@email.com",
                        "password": "senha789"
                    }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Erro de negócio"));
    }

    @Test
    @DisplayName("Deve negar cadastro com email inválido")
    void deveNegarCadastroEmailInvalido() throws Exception {
        String json = """
                    {
                        "name": "Felipe",
                        "email": "email-invalido",
                        "password": "senha123"
                    }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("Deve negar acesso sem token")
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isForbidden());
    }

//    @Test
//    @DisplayName("Deve negar acesso com token inválido")
//    void deveNegarAcessoTokenInvalido() throws Exception {
//        mockMvc.perform(get("/usuarios/me")
//                        .header("Authorization", "Bearer tokenInvalido123"))
//                .andExpect(status().isUnauthorized());
//    }
}
