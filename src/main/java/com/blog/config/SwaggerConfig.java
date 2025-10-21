package com.blog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .info(getInfo())
                .servers(getServers())
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Insira o token JWT obtido no endpoint /api/auth/login")
                                )
                );
    }

    private Info getInfo() {
        return new Info()
                .title("ThreadFlow API")
                .version("1.0.0")
                .description("""
                        API REST completa para gerenciamento de blog com sistema de autenticação JWT.
                        
                        ## Funcionalidades
                        - 🔐 Autenticação e Autorização com JWT
                        - 👥 Sistema de Roles (USER, ADMIN, MODERATOR)
                        - 📝 CRUD completo de Posts
                        - 💬 Sistema de Comentários
                        - 🔍 Busca e Filtros
                        - 📄 Paginação de resultados
                        - ✅ Validações de dados
                        
                        ## Como usar
                        1. Registre um usuário em `/api/auth/register`
                        2. Faça login em `/api/auth/login` para obter o token JWT
                        3. Clique no botão 'Authorize' (🔒) no topo da página
                        4. Cole o token no campo (sem o prefixo 'Bearer')
                        5. Use os endpoints protegidos normalmente
                        
                        ## Roles e Permissões
                        - **USER**: Criar, editar e deletar próprios posts/comentários
                        - **MODERATOR**: Deletar comentários de outros usuários
                        - **ADMIN**: Acesso total ao sistema
                        """)
                .contact(new Contact()
                        .name("ThreadFlow Team")
                        .email("contato@threadflow.com")
                        .url("https://github.com/seu-usuario/threadflow"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> getServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Servidor Local"),
                new Server()
                        .url("https://threadflow-api.com")
                        .description("Servidor de Produção")
        );
    }
}