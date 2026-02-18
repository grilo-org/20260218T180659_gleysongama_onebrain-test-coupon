package br.com.gama.cupon.domain.model;

import br.com.gama.cupon.domain.exception.InvalidCouponException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponCodeTest {

    @Test
    @DisplayName("Deve criar um CouponCode com 6 caracteres alfanuméricos válidos")
    void shouldCreateCouponCodeWithValidAlphanumericValue() {
        String validCode = "ABCDEF";
        CouponCode couponCode = new CouponCode(validCode);
        assertThat(couponCode.getValue()).isEqualTo(validCode);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Deve lançar exceção para código nulo ou vazio")
    void shouldThrowExceptionForNullEmptyOrBlankCode(String invalidCode) {
        assertThatThrownBy(() -> new CouponCode(invalidCode))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Coupon code cannot be null.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC", "ABCD", "ABCDE"}) // Menos de 6 caracteres
    @DisplayName("Deve preencher código com menos de 6 caracteres")
    void shouldFillCodeWithLessThanSixCharacters(String shortCode) {
        CouponCode couponCode = new CouponCode(shortCode);
        assertThat(couponCode.getValue()).hasSize(6);
        assertThat(couponCode.getValue()).matches("^[a-zA-Z0-9]+$");
        assertThat(couponCode.getValue()).startsWith(shortCode);
        assertThat(couponCode.getValue().charAt(shortCode.length())).isEqualTo('X');
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABCDEFG", "ABCDEFGH"}) // Mais de 6 caracteres
    @DisplayName("Deve truncar código com mais de 6 caracteres")
    void shouldTruncateCodeWithMoreThanSixCharacters(String longCode) {
        CouponCode couponCode = new CouponCode(longCode);
        assertThat(couponCode.getValue()).hasSize(6);
        assertThat(couponCode.getValue()).isEqualTo(longCode.substring(0, 6));
    }

    @Test
    @DisplayName("Deve sanitizar e truncar código com caracteres especiais e mais de 6 caracteres")
    void shouldSanitizeAndTruncateCodeWithSpecialCharsAndLongLength() {
        String input = "ABC-D$EFGH!"; // Alphanumeric: ABCDEFGH
        CouponCode couponCode = new CouponCode(input);
        assertThat(couponCode.getValue()).isEqualTo("ABCDEF"); // Truncado para 6
    }

    @Test
    @DisplayName("Deve sanitizar e preencher código com caracteres especiais e menos de 6 caracteres")
    void shouldSanitizeAndFillCodeWithSpecialCharsAndShortLength() {
        String input = "A-B#C$"; // Alphanumeric: ABC
        CouponCode couponCode = new CouponCode(input);
        assertThat(couponCode.getValue()).isEqualTo("ABCXXX"); // Preenchido para 6
    }
}
