package br.com.gama.cupon.infrastructure.adapter.in.web;

import br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa.CouponJpaEntity;
import br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa.JpaCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") // Garante que application-test.yml é carregado
class DeleteCouponControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JpaCouponRepository jpaCouponRepository;

    private UUID existingCouponId;
    private CouponJpaEntity existingCouponEntity;

    @BeforeEach
    void setUp() {
        jpaCouponRepository.deleteAll(); // Limpa o banco antes de cada teste

        existingCouponId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        existingCouponEntity = CouponJpaEntity.builder()
                .id(existingCouponId)
                .codeValue("EXIST1")
                .description("Existing Coupon Desc")
                .discountValue(BigDecimal.valueOf(15.00))
                .expirationDate(LocalDate.now().plusMonths(1))
                .published(true)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        jpaCouponRepository.save(existingCouponEntity);

        // Um cupom deletado para testes específicos de deleção
        CouponJpaEntity deletedCouponEntity = CouponJpaEntity.builder()
                .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .codeValue("DELETD")
                .description("Deleted Coupon Desc")
                .discountValue(BigDecimal.valueOf(5.00))
                .expirationDate(LocalDate.now().plusMonths(2))
                .published(false)
                .deleted(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        jpaCouponRepository.save(deletedCouponEntity);
    }

    @Test
    @DisplayName("DELETE /api/v1/coupons/{id} - Deve realizar soft delete de um cupom")
    void shouldSoftDeleteCoupon() {
        webTestClient.delete().uri("/api/v1/coupons/{id}", existingCouponId)
                .exchange()
                .expectStatus().isNoContent();

        // Verifica se o cupom foi marcado como deletado no banco de dados
        // Precisamos usar findByIdIgnoringDeleted para ver o estado real
        CouponJpaEntity deletedCoupon = jpaCouponRepository.findByIdIgnoringDeleted(existingCouponId).orElseThrow();
        assertThat(deletedCoupon.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("DELETE /api/v1/coupons/{id} - Deve retornar 404 para tentar deletar cupom não encontrado")
    void shouldReturnNotFoundForDeleteNonExistingCoupon() {
        UUID nonExistingId = UUID.randomUUID();
        webTestClient.delete().uri("/api/v1/coupons/{id}", nonExistingId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("DELETE /api/v1/coupons/{id} - Deve retornar 409 para tentar deletar cupom já deletado")
    void shouldReturnConflictForDeleteAlreadyDeletedCoupon() {
        UUID alreadyDeletedId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        // Tentar deletar um cupom que já está marcado como deleted = true
        webTestClient.delete().uri("/api/v1/coupons/{id}", alreadyDeletedId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Coupon with ID " + alreadyDeletedId + " is already deleted.");

        // Verificar que continua deletado
        CouponJpaEntity stillDeletedCoupon = jpaCouponRepository.findByIdIgnoringDeleted(alreadyDeletedId).orElseThrow();
        assertThat(stillDeletedCoupon.isDeleted()).isTrue();
    }
}
