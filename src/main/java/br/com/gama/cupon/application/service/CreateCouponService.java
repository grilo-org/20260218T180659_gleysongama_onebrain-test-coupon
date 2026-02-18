package br.com.gama.cupon.application.service;

import br.com.gama.cupon.application.command.CreateCouponCommand;
import br.com.gama.cupon.application.port.in.CreateCouponUseCase;
import br.com.gama.cupon.application.query.CouponResponse;
import br.com.gama.cupon.domain.exception.InvalidCouponException;
import br.com.gama.cupon.domain.model.Coupon;
import br.com.gama.cupon.domain.port.out.CouponRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateCouponService implements CreateCouponUseCase {

    private final CouponRepositoryPort couponRepositoryPort;

    @Override
    public CouponResponse createCoupon(CreateCouponCommand command) {
        couponRepositoryPort.findByCode(command.getCode()) // findByCode agora retorna APENAS cupons ATIVOS
                .ifPresent(existingCoupon -> {
                    throw new InvalidCouponException("Coupon code '" + command.getCode() + "' is already in use by another active coupon.");
                });

        Coupon newCoupon = Coupon.builder()
                .code(command.getCode())
                .description(command.getDescription())
                .discountValue(command.getDiscountValue())
                .expirationDate(command.getExpirationDate())
                .published(command.isPublished())
                .deleted(false)
                .build();

        Coupon savedCoupon = couponRepositoryPort.save(newCoupon);
        return CouponResponse.fromDomain(savedCoupon);
    }
}
