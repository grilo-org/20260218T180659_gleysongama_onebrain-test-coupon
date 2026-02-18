package br.com.gama.cupon.application.service;

import br.com.gama.cupon.domain.exception.CouponAlreadyDeletedException;
import br.com.gama.cupon.domain.exception.CouponNotFoundException;
import br.com.gama.cupon.domain.model.Coupon;
import br.com.gama.cupon.domain.model.CouponCode;
import br.com.gama.cupon.domain.port.out.CouponRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCouponServiceTest {

    @Mock
    private CouponRepositoryPort couponRepositoryPort;

    @InjectMocks
    private DeleteCouponService deleteCouponService;

    private UUID couponId;
    private Coupon activeCoupon;
    private Coupon deletedCoupon;

    @BeforeEach
    void setUp() {
        couponId = UUID.randomUUID();

        // Cupom ativo para testes
        activeCoupon = Coupon.builder()
                .id(couponId)
                .code(new CouponCode("ACTIVE123").toString())
                .description("Active Coupon")
                .discountValue(new BigDecimal("10.00"))
                .expirationDate(LocalDate.now().plusDays(30))
                .published(true)
                .deleted(false) // Este é um cupom ativo
                .build();

        // Cupom já deletado para testes
        deletedCoupon = Coupon.builder()
                .id(UUID.randomUUID()) // ID diferente para o cupom deletado
                .code(new CouponCode("DELETED456").toString())
                .description("Deleted Coupon")
                .discountValue(new BigDecimal("5.00"))
                .expirationDate(LocalDate.now().plusDays(10))
                .published(false)
                .deleted(true) // Este é um cupom já deletado
                .build();
    }

    @Test
    @DisplayName("Deve marcar um cupom existente como deletado")
    void shouldMarkExistingCouponAsDeleted() {
        when(couponRepositoryPort.findByIdIgnoringDeleted(couponId)).thenReturn(Optional.of(activeCoupon));
        when(couponRepositoryPort.save(any(Coupon.class))).thenReturn(activeCoupon);

        deleteCouponService.deleteCoupon(couponId);

        verify(couponRepositoryPort).findByIdIgnoringDeleted(couponId);
        verify(couponRepositoryPort).save(activeCoupon);
        assertThat(activeCoupon.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar CouponNotFoundException se o cupom não for encontrado")
    void shouldThrowCouponNotFoundExceptionWhenCouponDoesNotExist() {
        when(couponRepositoryPort.findByIdIgnoringDeleted(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteCouponService.deleteCoupon(UUID.randomUUID()))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessageContaining("Coupon with ID");

        verify(couponRepositoryPort, never()).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Deve lançar CouponAlreadyDeletedException se o cupom já estiver deletado")
    void shouldThrowCouponAlreadyDeletedExceptionWhenCouponIsAlreadyDeleted() {
        when(couponRepositoryPort.findByIdIgnoringDeleted(deletedCoupon.getId())).thenReturn(Optional.of(deletedCoupon));

        assertThatThrownBy(() -> deleteCouponService.deleteCoupon(deletedCoupon.getId()))
                .isInstanceOf(CouponAlreadyDeletedException.class)
                .hasMessage("Coupon with ID " + deletedCoupon.getId() + " is already deleted.");

        verify(couponRepositoryPort, never()).save(any(Coupon.class));
    }
}
