package br.com.gama.cupon.infrastructure.adapter.out.persistence.jpa;

import br.com.gama.cupon.domain.model.Coupon;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
//@Where(clause = "deleted = false")
public class CouponJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 6)
    private String codeValue;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private boolean deleted;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Converte de domínio para JPA
    public static CouponJpaEntity fromDomain(Coupon coupon) {
        return CouponJpaEntity.builder()
                .id(coupon.getId())
                .codeValue(coupon.getCode().getValue())
                .description(coupon.getDescription())
                .discountValue(coupon.getDiscountValue().getValue())
                .expirationDate(coupon.getExpirationDate().getValue())
                .published(coupon.isPublished())
                .deleted(coupon.isDeleted())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }

    // Converte de JPA para domínio
    public Coupon toDomain() {
        return Coupon.builder()
                .id(this.id)
                .code(this.codeValue)
                .description(this.description)
                .discountValue(this.discountValue)
                .expirationDate(this.expirationDate)
                .published(this.published)
                .deleted(this.deleted)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
