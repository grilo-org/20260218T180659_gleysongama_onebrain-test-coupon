package br.com.gama.cupon.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OpenApiConfigTest {

    @Autowired
    private ApplicationContext applicationContext; // Para verificar se o contexto carregou

    @Autowired
    private OpenAPI openApi; // Injeta o bean OpenAPI configurado

    @Test
    @DisplayName("O contexto da aplicação deve carregar com sucesso")
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("O bean OpenAPI deve ser criado e configurado corretamente")
    void openApiBeanShouldBeConfiguredCorrectly() {
        assertThat(openApi).isNotNull();

        Info info = openApi.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Gestão da API de Coupon");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getDescription()).isEqualTo("API para gerenciar cupons de desconto.");
        assertThat(info.getTermsOfService()).isEqualTo("http://swagger.io/terms/");

        License license = info.getLicense();
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("Apache 2.0");
        assertThat(license.getUrl()).isEqualTo("http://springdoc.org");
    }
}
