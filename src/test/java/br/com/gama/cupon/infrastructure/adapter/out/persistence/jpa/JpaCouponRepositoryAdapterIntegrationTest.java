package br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa;

import br.com.gama.cupon.domain.exception.CouponAlreadyDeletedException;
import br.com.gama.cupon.domain.model.Coupon;
import br.com.gama.cupon.domain.port.out.CouponRepositoryPort;
import br.com.gama.cupon.infrastructure.config.AuditConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest // Configura um banco de dados em memória (H2 por padrão)
@Import({JpaCouponRepositoryAdapter.class, AuditConfig.class}) // Importa o Adapter e a configuração de auditoria
@ActiveProfiles("test") // Ativa o perfil de teste, para carregar application-test.yml se existir
class JpaCouponRepositoryAdapterIntegrationTest {

    @Autowired
    private CouponRepositoryPort couponRepositoryPort;

    @Autowired
    private JpaCouponRepository jpaCouponRepository;

    @BeforeEach
    void setUp() {
        jpaCouponRepository.deleteAllInBatch(); // Garante um estado limpo antes de cada teste
    }

    @Test
    @DisplayName("Deve salvar um novo cupom e recuperá-lo por ID")
    void shouldSaveAndFindCouponById() {
        Coupon coupon = Coupon.builder()
                .code("SAVE01")
                .description("Test Save")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(true)
                .build();

        Coupon savedCoupon = couponRepositoryPort.save(coupon);

        assertThat(savedCoupon).isNotNull();
        assertThat(savedCoupon.getId()).isNotNull();
        assertThat(savedCoupon.getCode().getValue()).isEqualTo("SAVE01");

        Optional<Coupon> foundCoupon = couponRepositoryPort.findById(savedCoupon.getId());
        assertThat(foundCoupon).isPresent();
        assertThat(foundCoupon.get().getCode().getValue()).isEqualTo("SAVE01");
        assertThat(foundCoupon.get().isDeleted()).isFalse();
    }

    @Test
    @DisplayName("Não deve encontrar cupom por ID se estiver deletado")
    void shouldNotFindDeletedCouponById() {
        Coupon coupon = Coupon.builder()
                .code("DEL001")
                .description("Test Deleted")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(true)
                .deleted(true)
                .build();
        couponRepositoryPort.save(coupon);

        Optional<Coupon> foundCoupon = couponRepositoryPort.findById(coupon.getId());
        assertThat(foundCoupon).isNotPresent();
    }

    @Test
    @DisplayName("Deve realizar soft delete em um cupom e não recuperá-lo por findById")
    void shouldSoftDeleteCouponAndNotFindById() {
        Coupon coupon = Coupon.builder()
                .code("SOFTDL")
                .description("Test Soft Delete")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(true)
                .build();
        Coupon savedCoupon = couponRepositoryPort.save(coupon);

        savedCoupon.markAsDeleted();
        couponRepositoryPort.save(savedCoupon);

        Optional<Coupon> foundCoupon = couponRepositoryPort.findById(savedCoupon.getId());
        assertThat(foundCoupon).isNotPresent();
    }

    @Test
    @DisplayName("Deve encontrar cupom por código")
    void shouldFindCouponByCode() {
        Coupon coupon = Coupon.builder()
                .code("CODE01")
                .description("Test Code Search")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(true)
                .build();
        couponRepositoryPort.save(coupon);

        Optional<Coupon> foundCoupon = couponRepositoryPort.findByCode("CODE01");
        assertThat(foundCoupon).isPresent();
        assertThat(foundCoupon.get().getDescription()).isEqualTo("Test Code Search");
    }

    @Test
    @DisplayName("Não deve encontrar cupom por código se estiver deletado")
    void shouldNotFindDeletedCouponByCode() {
        Coupon coupon = Coupon.builder()
                .code("CODEDEL")
                .description("Test Deleted Code")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(true)
                .deleted(true)
                .build();
        couponRepositoryPort.save(coupon);

        Optional<Coupon> foundCoupon = couponRepositoryPort.findByCode("CODEDEL");
        assertThat(foundCoupon).isNotPresent();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar soft delete de cupom já deletado")
    void shouldThrowExceptionWhenSoftDeletingAlreadyDeletedCoupon() {
        Coupon coupon = Coupon.builder()
                .code("ALREADY")
                .description("Already Deleted")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(true)
                .deleted(true)
                .build();
        couponRepositoryPort.save(coupon);

        // Para este teste, precisamos buscar o CouponJpaEntity para conseguir simular o cenário de
        // um cupom `deleted=true` que foi recuperado de alguma forma (ex: consulta administrativa)
        // e se tenta deletá-lo novamente. O método findById da porta já ignora deleted=true.
        Optional<CouponJpaEntity> entityOptional = jpaCouponRepository.findById(coupon.getId());
        assertThat(entityOptional).isPresent();
        Coupon retrievedDeletedCoupon = entityOptional.get().toDomain();

        assertThatThrownBy(retrievedDeletedCoupon::markAsDeleted)
                .isInstanceOf(CouponAlreadyDeletedException.class)
                .hasMessageContaining("already deleted");
    }

    @Test
    @DisplayName("Deve listar apenas cupons não deletados")
    void shouldListOnlyNonDeletedCoupons() {
        Coupon coupon1 = Coupon.builder().code("CUPOM1").description("Cupom 1").discountValue(new BigDecimal("10.00")).expirationDate(LocalDate.now().plusDays(10)).published(true).build();
        Coupon coupon2 = Coupon.builder().code("CUPOM2").description("Cupom 2").discountValue(new BigDecimal("10.00")).expirationDate(LocalDate.now().plusDays(10)).published(true).deleted(true).build(); // Deletado
        Coupon coupon3 = Coupon.builder().code("CUPOM3").description("Cupom 3").discountValue(new BigDecimal("10.00")).expirationDate(LocalDate.now().plusDays(10)).published(true).build();

        couponRepositoryPort.save(coupon1);
        couponRepositoryPort.save(coupon2);
        couponRepositoryPort.save(coupon3);

        List<Coupon> activeCoupons = ((JpaCouponRepositoryAdapter) couponRepositoryPort).findAll(); // Chamada específica do adapter

        assertThat(activeCoupons).hasSize(2);
        assertThat(activeCoupons).extracting(c -> c.getCode().getValue()).containsExactlyInAnyOrder("CUPOM1", "CUPOM3");
    }
}
