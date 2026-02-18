package br.com.gama.cupon.infrastructure.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    // Mesmo não fazendo parte do proceso seletivo, optei em deixar essa classe como uma v2 do projeto
    // Para evolução do projeto de cupons, essa classe pode ser usada para configurar beans globais, como um Mapper para
    // DTOs (ex: ModelMapper, MapStruct) ou outras configurações que não se encaixem em OpenApiConfig ou AuditConfig.
}
