package br.com.gama.cupon.domain.model;

import br.com.gama.cupon.domain.exception.InvalidCouponException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@EqualsAndHashCode
@ToString
public class DiscountValue {

    private static final BigDecimal MIN_DISCOUNT_VALUE = new BigDecimal("0.5");

    private final BigDecimal value;

    public DiscountValue(BigDecimal value) {
        Objects.requireNonNull(value, "Discount value cannot be null.");
        validateValue(value);
        this.value = value;
    }

    private void validateValue(BigDecimal value) {
        if (value.compareTo(MIN_DISCOUNT_VALUE) < 0) {
            throw new InvalidCouponException("Discount value cannot be less than " + MIN_DISCOUNT_VALUE);
        }
    }
}
