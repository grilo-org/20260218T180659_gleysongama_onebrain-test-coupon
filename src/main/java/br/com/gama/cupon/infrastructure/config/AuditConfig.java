package br.com.gama.cupon.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // Habilita a auditoria do JPA (para @CreatedDate, @LastModifiedDate)
public class AuditConfig {
    // Por default a classe pode ficar vazia, a anotação @EnableJpaAuditing já faz o trabalho
}
