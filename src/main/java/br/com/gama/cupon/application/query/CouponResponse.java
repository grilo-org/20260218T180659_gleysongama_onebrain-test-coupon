package br.com.gama.cupon.application.query;

import br.com.gama.cupon.domain.model.Coupon;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CouponResponse {
    private UUID id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDate expirationDate;
    private boolean published;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CouponResponse fromDomain(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode().getValue())
                .description(coupon.getDescription())
                .discountValue(coupon.getDiscountValue().getValue())
                .expirationDate(coupon.getExpirationDate().getValue())
                .published(coupon.isPublished())
                .deleted(coupon.isDeleted())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }
}
