package com.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username = "felipe", roles = {"USER"})
    @Test
    public void deveCriarPostComDadosValidos() throws Exception {
        String json = """
                    {
                        "usuarioId": 11,
                        "titulo": "Meu primeiro post",
                        "conteudo": "Conteúdo do post de teste"
                    }
                """;

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Meu primeiro post"))
                .andExpect(jsonPath("$.conteudo").value("Conteúdo do post de teste"));
    }

    @WithMockUser(username = "felipe", roles = {"USER"})
    @Test
    public void deveBuscarPostPorId() throws Exception {
        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @WithMockUser(username = "felipe", roles = {"USER"})
    @Test
    public void deveListarTodosOsPosts() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @WithMockUser(username = "felipe", roles = {"USER"})
    @Test
    public void deveAtualizarPost() throws Exception {
        String json = """
                    {
                        "usuarioId": 11,
                        "titulo": "Título atualizado",
                        "conteudo": "Conteúdo atualizado"
                    }
                """;

        mockMvc.perform(put("/posts/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Título atualizado"));
    }
}
