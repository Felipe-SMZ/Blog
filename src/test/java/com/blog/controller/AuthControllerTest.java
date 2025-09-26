package com.blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void deveGerarTokenComCredenciaisValidas() throws Exception {
        String json = """
                    {
                        "email": "jiraya@email.com",
                        "password": "147852"
                    }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void deveNegarLoginComCredenciaisInvalidas() throws Exception {
        String json = """
                    {
                        "email": "email@invalido.com",
                        "password": "errada"
                    }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveCadastrarNovoUsuarioComSucesso() throws Exception {
        String json = """
                    {
                        "name": "Naruto Uzumaki",
                        "email": "naruto@email.com",
                        "password": "rasengan123"
                    }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Naruto Uzumaki"))
                .andExpect(jsonPath("$.email").value("naruto@email.com"));
    }

    @Test
    public void deveNegarCadastroComEmailDuplicado() throws Exception {
        String json = """
                    {
                        "name": "Naruto Uzumaki",
                        "email": "narutoteste@email.com",
                        "password": "rasengan123"
                    }
                """;

        // 1º cadastro — deve funcionar
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        // 2º cadastro com mesmo e-mail — deve falhar
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("E-mail duplicado"));
    }

    @Test
    public void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/usuarios/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deveNegarAcessoComTokenInvalido() throws Exception {
        String tokenFalso = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fake.payload.signature";

        mockMvc.perform(get("/usuarios/me")
                        .header("Authorization", tokenFalso))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveNegarCadastroComNomeVazio() throws Exception {
        String json = """
                    {
                        "name": "",
                        "email": "teste@email.com",
                        "password": "123456"
                    }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    public void deveNegarCadastroComEmailInvalido() throws Exception {
        String json = """
                    {
                        "name": "Felipe",
                        "email": "email-invalido",
                        "password": "123456"
                    }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Formato de e-mail incorreto"));
    }

    @Test
    public void deveNegarCadastroSemSenha() throws Exception {
        String json = """
                    {
                        "name": "Felipe",
                        "email": "felipe@email.com",
                        "password": ""
                    }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A senha é obrigatória"));
    }


}
