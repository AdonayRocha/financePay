package com.financePay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinancePay API")
                        .version("1.0.0")
                        .description("API para gestão de saldos financeiros")
                        .contact(new Contact()
                                .name("Equipe: Adonay Rodrigues da Rocha, Pedro Henrique Martins dos Res")
                                .email("rm558782@fiap.com.br, rm555306@fiap.com.br")));
            
    }
}
