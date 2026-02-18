package br.com.gama.cupon.domain.model;

import br.com.gama.cupon.domain.exception.CouponAlreadyDeletedException;
import br.com.gama.cupon.domain.exception.InvalidCouponException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @Test
    @DisplayName("Deve criar um cupom válido com todos os atributos")
    void shouldCreateValidCoupon() {
        UUID id = UUID.randomUUID();
        String code = "ABCDE1";
        String description = "Desconto de 10%";
        BigDecimal discountValue = new BigDecimal("10.00");
        LocalDate expirationDate = LocalDate.now().plusDays(7);
        boolean published = true;

        Coupon coupon = Coupon.builder()
                .id(id)
                .code(code)
                .description(description)
                .discountValue(discountValue)
                .expirationDate(expirationDate)
                .published(published)
                .build();

        assertThat(coupon).isNotNull();
        assertThat(coupon.getId()).isEqualTo(id);
        assertThat(coupon.getCode().getValue()).isEqualTo(code);
        assertThat(coupon.getDescription()).isEqualTo(description);
        assertThat(coupon.getDiscountValue().getValue()).isEqualTo(discountValue);
        assertThat(coupon.getExpirationDate().getValue()).isEqualTo(expirationDate);
        assertThat(coupon.isPublished()).isTrue();
        assertThat(coupon.isDeleted()).isFalse();
        assertThat(coupon.getCreatedAt()).isNotNull();
        assertThat(coupon.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve sanitizar código com caracteres especiais e manter 6 caracteres")
    void shouldSanitizeCouponCodeAndKeepLength() {
        String codeWithSpecialChars = "A-B#C$DE";
        Coupon coupon = Coupon.builder()
                .code(codeWithSpecialChars)
                .description("Test")
                .discountValue(BigDecimal.TEN)
                .expirationDate(LocalDate.now().plusDays(1))
                .build();
        assertThat(coupon.getCode().getValue()).isEqualTo("ABCDEX"); // Esperado 6, aqui seria 5.
        // O Value Object CouponCode já faz o trabalho de garantir o tamanho.
        // Se a sanitização resultar em menos de 6 caracteres, ele deve preencher.
        // Se resultar em mais de 6 caracteres, ele deve truncar.
        assertThat(coupon.getCode().getValue()).hasSize(6);
        assertThat(coupon.getCode().getValue()).matches("^[a-zA-Z0-9]+$");
    }

    @Test
    @DisplayName("Deve lançar exceção se valor de desconto for menor que 0.5")
    void shouldThrowExceptionForInvalidDiscountValue() {
        BigDecimal invalidDiscount = new BigDecimal("0.49");
        assertThatThrownBy(() -> Coupon.builder().code("ABCDEF").description("Desc").discountValue(invalidDiscount).expirationDate(LocalDate.now().plusDays(1)).build())
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("cannot be less than 0.5");
    }

    @Test
    @DisplayName("Deve lançar exceção se data de expiração for no passado")
    void shouldThrowExceptionForPastExpirationDate() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        assertThatThrownBy(() -> Coupon.builder().code("ABCDEF").description("Desc").discountValue(BigDecimal.TEN).expirationDate(pastDate).build())
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("cannot be in the past");
    }

    @Test
    @DisplayName("Deve marcar o cupom como deletado (soft delete)")
    void shouldMarkCouponAsDeleted() {
        Coupon coupon = Coupon.builder()
                .code("ABCDEF")
                .description("Test")
                .discountValue(BigDecimal.TEN)
                .expirationDate(LocalDate.now().plusDays(1))
                .build();

        coupon.markAsDeleted();

        assertThat(coupon.isDeleted()).isTrue();
        assertThat(coupon.getUpdatedAt()).isAfter(coupon.getCreatedAt());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar cupom já deletado")
    void shouldThrowExceptionWhenDeletingAlreadyDeletedCoupon() {
        Coupon coupon = Coupon.builder()
                .code("ABCDEF")
                .description("Test")
                .discountValue(BigDecimal.TEN)
                .expirationDate(LocalDate.now().plusDays(1))
                .deleted(true) // Simula um cupom já deletado
                .build();

        assertThatThrownBy(coupon::markAsDeleted)
                .isInstanceOf(CouponAlreadyDeletedException.class)
                .hasMessageContaining("already deleted");
    }
}
