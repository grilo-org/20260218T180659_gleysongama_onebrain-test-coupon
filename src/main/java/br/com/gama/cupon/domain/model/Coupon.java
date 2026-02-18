package br.com.gama.cupon.domain.model;

import br.com.gama.cupon.domain.exception.CouponAlreadyDeletedException;
import br.com.gama.cupon.domain.exception.InvalidCouponException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter(lombok.AccessLevel.PACKAGE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@ToString
public class Coupon {

    private UUID id;
    private CouponCode code;
    private String description;
    private DiscountValue discountValue;
    private ExpirationDate expirationDate;
    private boolean published;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Coupon(
            UUID id,
            String code,
            String description,
            BigDecimal discountValue,
            LocalDate expirationDate,
            boolean published,
            boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.setCode(code);
        this.description = description;
        this.setDiscountValue(discountValue);
        this.setExpirationDate(expirationDate);
        this.published = published;
        this.deleted = deleted;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();

        validateState();
    }

    private void validateState() {
        if (this.code == null || this.code.getValue().isBlank()) {
            throw new InvalidCouponException("Coupon code cannot be empty.");
        }
        if (this.description == null || this.description.isBlank()) {
            throw new InvalidCouponException("Coupon description cannot be empty.");
        }
        if (this.discountValue == null) {
            throw new InvalidCouponException("Coupon discount value cannot be null.");
        }
        if (this.expirationDate == null) {
            throw new InvalidCouponException("Coupon expiration date cannot be null.");
        }
    }

    public void setCode(String code) {
        this.code = new CouponCode(code);
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = new DiscountValue(discountValue);
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = new ExpirationDate(expirationDate);
    }

    public void markAsDeleted() {
        if (this.deleted) {
            throw new CouponAlreadyDeletedException("Coupon with ID " + id + " is already deleted.");
        }
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return this.expirationDate.isExpired();
    }

    public boolean isValid() {
        return !isDeleted() && !isExpired();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon = (Coupon) o;
        return Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
