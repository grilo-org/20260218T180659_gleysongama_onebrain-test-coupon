package br.com.gama.cupon.infrastructure.adapter.in.web;

import br.com.gama.cupon.application.query.CouponResponse;
import br.com.gama.cupon.infrastructure.adapter.in.web.dto.CouponRequestDTO;
import br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa.CouponJpaEntity;
import br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa.JpaCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") // Garante que application-test.yml é carregado
class CreateCouponControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JpaCouponRepository jpaCouponRepository;

    private UUID existingCouponId;
    private CouponJpaEntity existingCouponEntity;

    @BeforeEach
    void setUp() {
        jpaCouponRepository.deleteAllInBatch();

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
    @DisplayName("POST /api/v1/coupons - Deve criar um novo cupom com sucesso")
    void shouldCreateNewCouponSuccessfully() {
        CouponRequestDTO requestDTO = new CouponRequestDTO();
        requestDTO.setCode("NEWCOP");
        requestDTO.setDescription("New Coupon Description");
        requestDTO.setDiscountValue(BigDecimal.valueOf(20.00));
        requestDTO.setExpirationDate(LocalDate.now().plusDays(30));
        requestDTO.setPublished(true);

        webTestClient.post().uri("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CouponResponse.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getCode()).isEqualTo("NEWCOP");
                    assertThat(response.getDescription()).isEqualTo("New Coupon Description");
                    assertThat(response.getDiscountValue()).isEqualTo(BigDecimal.valueOf(20.00));
                    assertThat(response.getExpirationDate()).isEqualTo(LocalDate.now().plusDays(30));
                    assertThat(response.isPublished()).isTrue();
                    assertThat(response.isDeleted()).isFalse();
                });

        assertThat(jpaCouponRepository.findByCodeValue("NEWCOP")).isPresent();
    }

    @Test
    @DisplayName("POST /api/v1/coupons - Deve retornar 400 para requisição de criação inválida (código curto)")
    void shouldReturnBadRequestForInvalidCreateCouponRequest() {
        CouponRequestDTO requestDTO = new CouponRequestDTO();
        requestDTO.setCode("SHORT"); // Código muito curto
        requestDTO.setDescription("Invalid Description");
        requestDTO.setDiscountValue(BigDecimal.valueOf(10.00));
        requestDTO.setExpirationDate(LocalDate.now().plusDays(10));
        requestDTO.setPublished(true);

        webTestClient.post().uri("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/v1/coupons - Deve retornar 400 Bad Request se o código já estiver em uso por um cupom ativo")
    void shouldReturnBadRequestWhenCreatingWithExistingActiveCode() {
        // Given: existingCouponId já tem o código "EXIST1" e deleted=false (ativo)
        String duplicateCode = "EXIST1";

        CouponRequestDTO requestDTO = new CouponRequestDTO();
        requestDTO.setCode(duplicateCode); // Tenta usar um código já ativo
        requestDTO.setDescription("Attempt to create duplicate active code");
        requestDTO.setDiscountValue(BigDecimal.valueOf(10.00));
        requestDTO.setExpirationDate(LocalDate.now().plusDays(10));
        requestDTO.setPublished(true);

        webTestClient.post().uri("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Coupon code '" + duplicateCode + "' is already in use by another active coupon.");
    }

    @Test
    @DisplayName("POST /api/v1/coupons - Deve criar um novo cupom com sucesso se o código for usado apenas por cupom deletado")
    void shouldCreateNewCouponSuccessfullyIfCodeIsUsedByDeletedCouponOnly() {
        // Given: No setup, um cupom com código "DELETD" já existe, mas com deleted=true.
        // O código do cupom "DELETD" está no setup do BeforeEach como um cupom deletado.
        String codeUsedByDeleted = "DELETD";

        CouponRequestDTO requestDTO = new CouponRequestDTO();
        requestDTO.setCode(codeUsedByDeleted); // Usa um código de cupom deletado
        requestDTO.setDescription("New active coupon with code from a deleted one");
        requestDTO.setDiscountValue(BigDecimal.valueOf(18.00));
        requestDTO.setExpirationDate(LocalDate.now().plusDays(20));
        requestDTO.setPublished(true);

        webTestClient.post().uri("/api/v1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CouponResponse.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getCode()).isEqualTo(codeUsedByDeleted);
                    assertThat(response.getDescription()).isEqualTo("New active coupon with code from a deleted one");
                    assertThat(response.isDeleted()).isFalse(); // O novo cupom é ativo
                });

        // Verifique se agora há dois cupons com o código "DELETD" no banco de dados
        // (um deleted=true e outro deleted=false)
        List<CouponJpaEntity> couponsWithSameCode = jpaCouponRepository.findAll()
                .stream()
                .filter(c -> c.getCodeValue().equals(codeUsedByDeleted))
                .toList();

        assertThat(couponsWithSameCode).hasSize(2);
        assertThat(couponsWithSameCode).anyMatch(CouponJpaEntity::isDeleted); // Um deletado
        assertThat(couponsWithSameCode).anyMatch(c -> !c.isDeleted()); // Um ativo
    }
}
