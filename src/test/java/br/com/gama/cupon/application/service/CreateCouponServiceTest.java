package br.com.gama.cupon.application.service;

import br.com.gama.cupon.application.command.CreateCouponCommand;
import br.com.gama.cupon.application.query.CouponResponse;
import br.com.gama.cupon.domain.exception.InvalidCouponException;
import br.com.gama.cupon.domain.model.Coupon;
import br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa.JpaCouponRepositoryAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCouponServiceTest {

    @Mock
    private JpaCouponRepositoryAdapter couponRepositoryPort;

    @InjectMocks
    private CreateCouponService createCouponService;

    @Test
    @DisplayName("Deve criar um cupom com sucesso")
    void shouldCreateCouponSuccessfully() {
        // Given
        CreateCouponCommand command = CreateCouponCommand.builder()
                .code("NEWCODE")
                .description("New coupon")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(7))
                .published(true)
                .build();

        Coupon savedCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code(command.getCode())
                .description(command.getDescription())
                .discountValue(command.getDiscountValue())
                .expirationDate(command.getExpirationDate())
                .published(command.isPublished())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(couponRepositoryPort.save(any(Coupon.class))).thenReturn(savedCoupon);

        // When
        CouponResponse response = createCouponService.createCoupon(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("NEWCOD");
        assertThat(response.getDescription()).isEqualTo("New coupon");
        assertThat(response.isDeleted()).isFalse();
        verify(couponRepositoryPort, times(1)).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Deve lançar InvalidCouponException ao tentar criar cupom com código já em uso por cupom ativo")
    void shouldThrowInvalidCouponExceptionWhenCreatingWithExistingActiveCode() {
        // Given
        String duplicateCode = "DUPLI1";
        CreateCouponCommand command = CreateCouponCommand.builder()
                .code(duplicateCode)
                .description("Duplicate coupon attempt")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(7))
                .published(true)
                .build();

        Coupon existingActiveCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code(duplicateCode)
                .description("Original active coupon")
                .discountValue(new BigDecimal("5.00"))
                .expirationDate(LocalDate.now().plusDays(30))
                .published(true)
                .deleted(false) // ESTE É UM CUPOM ATIVO!
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Configura o mock para retornar o cupom ativo quando findByCode for chamado
        when(couponRepositoryPort.findByCode(duplicateCode)).thenReturn(Optional.of(existingActiveCoupon));

        // When / Then
        assertThatThrownBy(() -> createCouponService.createCoupon(command))
                .isInstanceOf(InvalidCouponException.class)
                .hasMessageContaining("Coupon code '" + duplicateCode + "' is already in use by another active coupon.");

        verify(couponRepositoryPort, times(1)).findByCode(duplicateCode);
        verify(couponRepositoryPort, never()).save(any(Coupon.class)); // O save não deve ser chamado
    }

    @Test
    @DisplayName("Deve criar um cupom com sucesso, mesmo se o código for usado por um cupom deletado")
    void shouldCreateCouponSuccessfullyEvenIfCodeIsUsedByDeletedCoupon() {
        // Given
        String codeUsedByDeleted = "DELETD";
        CreateCouponCommand command = CreateCouponCommand.builder()
                .code(codeUsedByDeleted)
                .description("New coupon, code used by deleted one")
                .discountValue(new BigDecimal("12.00"))
                .expirationDate(LocalDate.now().plusDays(14))
                .published(true)
                .build();

        // Configura o mock para NÃO encontrar um cupom ATIVO com o mesmo código.
        // Isso é crucial: findByCode(code) deve retornar Optional.empty() para este cenário.
        when(couponRepositoryPort.findByCode(codeUsedByDeleted)).thenReturn(Optional.empty());

        // Configura o mock para simular o salvamento do novo cupom
        Coupon newSavedCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code(codeUsedByDeleted)
                .description(command.getDescription())
                .discountValue(command.getDiscountValue())
                .expirationDate(command.getExpirationDate())
                .published(command.isPublished())
                .deleted(false) // Importante: o novo cupom é ativo!
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(couponRepositoryPort.save(any(Coupon.class))).thenReturn(newSavedCoupon);

        // When
        CouponResponse response = createCouponService.createCoupon(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(codeUsedByDeleted);
        assertThat(response.isDeleted()).isFalse(); // O novo cupom é ativo
        verify(couponRepositoryPort, times(1)).findByCode(codeUsedByDeleted);
        verify(couponRepositoryPort, times(1)).save(any(Coupon.class)); // O save deve ser chamado
    }
}
