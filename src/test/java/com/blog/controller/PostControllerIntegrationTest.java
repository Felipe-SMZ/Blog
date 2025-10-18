package com.blog.controller;

import com.blog.model.Post;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("PostController - Testes de Integração")
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String tokenUser;
    private String tokenAdmin;
    private Usuario usuarioNormal;
    private Usuario usuarioAdmin;
    private Post post;

    @BeforeEach
    void setUp() throws Exception {
        postRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Cria usuário normal
        usuarioNormal = new Usuario();
        usuarioNormal.setName("Felipe");
        usuarioNormal.setEmail("felipe@teste.com");
        usuarioNormal.setPassword(passwordEncoder.encode("senha123"));
        usuarioNormal.setRole(Role.USER);
        usuarioRepository.save(usuarioNormal);

        // Cria usuário admin
        usuarioAdmin = new Usuario();
        usuarioAdmin.setName("Admin");
        usuarioAdmin.setEmail("admin@teste.com");
        usuarioAdmin.setPassword(passwordEncoder.encode("admin123"));
        usuarioAdmin.setRole(Role.ADMIN);
        usuarioRepository.save(usuarioAdmin);

        // Login do usuário normal e extrai token
        String loginUser = """
                    {
                        "email": "felipe@teste.com",
                        "password": "senha123"
                    }
                """;

        MvcResult resultUser = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginUser))
                .andExpect(status().isOk())
                .andReturn();

        String responseUser = resultUser.getResponse().getContentAsString();
        tokenUser = objectMapper.readTree(responseUser).get("token").asText();

        // Login do admin e extrai token
        String loginAdmin = """
                    {
                        "email": "admin@teste.com",
                        "password": "admin123"
                    }
                """;

        MvcResult resultAdmin = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginAdmin))
                .andExpect(status().isOk())
                .andReturn();

        String responseAdmin = resultAdmin.getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(responseAdmin).get("token").asText();

        // Cria um post
        post = new Post();
        post.setUsuario(usuarioNormal);
        post.setTitulo("Post Teste");
        post.setConteudo("Conteúdo teste");
        postRepository.save(post);
    }

    @Test
    @DisplayName("Deve criar post com usuário autenticado")
    void deveCriarPostComAutenticacao() throws Exception {
        String json = """
                    {
                        "titulo": "Novo Post",
                        "conteudo": "Conteúdo do novo post"
                    }
                """;

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenUser)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Novo Post"))
                .andExpect(jsonPath("$.conteudo").value("Conteúdo do novo post"));
    }

    @Test
    @DisplayName("Deve negar criar post sem autenticação")
    void deveNegarCriarPostSemAutenticacao() throws Exception {
        String json = """
                    {
                        "titulo": "Post Sem Token",
                        "conteudo": "Conteúdo"
                    }
                """;

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve listar posts com paginação")
    void deveListarPostsComPaginacao() throws Exception {
        mockMvc.perform(get("/posts?page=0&size=10")
                        .header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("Deve buscar post por ID")
    void deveBuscarPostPorId() throws Exception {
        mockMvc.perform(get("/posts/" + post.getId())
                        .header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Post Teste"));
    }

    @Test
    @DisplayName("Deve buscar posts por título")
    void deveBuscarPostsPorTitulo() throws Exception {
        mockMvc.perform(get("/posts/buscar/titulo?titulo=Post&page=0&size=10")
                        .header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("USER consegue atualizar seu próprio post")
    void userAtualizaProprioPost() throws Exception {
        String json = """
                    {
                        "titulo": "Post Atualizado",
                        "conteudo": "Conteúdo atualizado"
                    }
                """;

        mockMvc.perform(put("/posts/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenUser)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Post Atualizado"));
    }

    @Test
    @DisplayName("USER não consegue atualizar post alheio")
    void userNaoAtualizaPostAlheio() throws Exception {
        // Cria outro usuário
        Usuario outroUsuario = new Usuario();
        outroUsuario.setName("Outro");
        outroUsuario.setEmail("outro@teste.com");
        outroUsuario.setPassword(passwordEncoder.encode("senha456"));
        outroUsuario.setRole(Role.USER);
        usuarioRepository.save(outroUsuario);

        // Faz login como outro usuário
        String login = """
                    {
                        "email": "outro@teste.com",
                        "password": "senha456"
                    }
                """;

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String tokenOutro = objectMapper.readTree(response).get("token").asText();

        // Tenta atualizar post do outro
        String updateJson = """
                    {
                        "titulo": "Hacker",
                        "conteudo": "Hackeado"
                    }
                """;

        mockMvc.perform(put("/posts/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenOutro)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN consegue deletar qualquer post")
    void adminDeletaQualquerPost() throws Exception {
        mockMvc.perform(delete("/posts/" + post.getId())
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("USER não consegue deletar post alheio")
    void userNaoDeletaPostAlheio() throws Exception {
        // Cria outro usuário
        Usuario outroUsuario = new Usuario();
        outroUsuario.setName("Outro");
        outroUsuario.setEmail("outro2@teste.com");
        outroUsuario.setPassword(passwordEncoder.encode("senha789"));
        outroUsuario.setRole(Role.USER);
        usuarioRepository.save(outroUsuario);

        // Faz login
        String login = """
                    {
                        "email": "outro2@teste.com",
                        "password": "senha789"
                    }
                """;

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String tokenOutro = objectMapper.readTree(response).get("token").asText();

        // Tenta deletar
        mockMvc.perform(delete("/posts/" + post.getId())
                        .header("Authorization", "Bearer " + tokenOutro))
                .andExpect(status().isForbidden());
    }
}