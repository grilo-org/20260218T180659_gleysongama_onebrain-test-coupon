package br.com.gama.cupon.domain.port.out;

import br.com.gama.cupon.domain.model.Coupon;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepositoryPort {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(UUID id);
    Optional<Coupon> findByIdIgnoringDeleted(UUID id);
    Optional<Coupon> findByCode(String code);
    Optional<Coupon> findByIdIncludingDeleted(UUID id);
    List<Coupon> findAll();
}
