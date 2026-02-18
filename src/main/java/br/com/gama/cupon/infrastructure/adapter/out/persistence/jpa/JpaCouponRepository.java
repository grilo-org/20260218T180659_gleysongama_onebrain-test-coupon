package br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCouponRepository extends JpaRepository<CouponJpaEntity, UUID> {
    Optional<CouponJpaEntity> findByIdAndDeletedFalse(UUID id);

    Optional<CouponJpaEntity> findByCodeValueAndDeletedFalse(String codeValue);

    List<CouponJpaEntity> findAllByDeletedFalse();

    // Método para buscar por código, respeitando o @Where(deleted = false)
    Optional<CouponJpaEntity> findByCodeValue(String codeValue);

    // Método para buscar um cupom por ID, incluindo os deletados,
    // ignorando a cláusula @Where global para este caso específico.
    @Query("SELECT c FROM CouponJpaEntity c WHERE c.id = :id")
    Optional<CouponJpaEntity> findByIdIgnoringDeleted(UUID id);
}
