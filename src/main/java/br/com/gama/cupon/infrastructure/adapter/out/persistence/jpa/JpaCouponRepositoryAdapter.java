package br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa;

import br.com.gama.cupon.domain.model.Coupon;
import br.com.gama.cupon.domain.port.out.CouponRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaCouponRepositoryAdapter implements CouponRepositoryPort {

    private final JpaCouponRepository jpaCouponRepository;

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity entity = CouponJpaEntity.fromDomain(coupon);
        CouponJpaEntity savedEntity = jpaCouponRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return jpaCouponRepository.findByIdAndDeletedFalse(id).map(CouponJpaEntity::toDomain);
    }

    @Override
    public Optional<Coupon> findByIdIgnoringDeleted(UUID id) {
        return jpaCouponRepository.findByIdIgnoringDeleted(id).map(CouponJpaEntity::toDomain);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return jpaCouponRepository.findByCodeValueAndDeletedFalse(code).map(CouponJpaEntity::toDomain);
    }

    @Override
    public Optional<Coupon> findByIdIncludingDeleted(UUID id) {
        return jpaCouponRepository.findByIdIgnoringDeleted(id).map(CouponJpaEntity::toDomain);
    }

    @Override
    public List<Coupon> findAll() {
        return jpaCouponRepository.findAllByDeletedFalse()
                .stream()
                .map(CouponJpaEntity::toDomain)
                .toList();
    }
}
