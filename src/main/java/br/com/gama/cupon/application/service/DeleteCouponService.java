package br.com.gama.cupon.application.service;

import br.com.gama.cupon.application.port.in.DeleteCouponUseCase;
import br.com.gama.cupon.domain.exception.CouponNotFoundException;
import br.com.gama.cupon.domain.model.Coupon;
import br.com.gama.cupon.domain.port.out.CouponRepositoryPort;
import br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa.JpaCouponRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCouponService implements DeleteCouponUseCase {

    private final CouponRepositoryPort couponRepositoryPort;

    @Override
    public void deleteCoupon(UUID id) {
        Coupon foundCoupon = couponRepositoryPort.findByIdIgnoringDeleted(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon with ID " + id + " not found."));

        // A lógica de markAsDeleted() na entidade Coupon já verifica se o cupom já está deletado
        // e lança CouponAlreadyDeletedException, atendendo à regra de negócio.
        foundCoupon.markAsDeleted();
        couponRepositoryPort.save(foundCoupon);
    }
}
