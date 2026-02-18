package br.com.gama.cupon.domain.model;

import br.com.gama.cupon.domain.exception.InvalidCouponException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@EqualsAndHashCode
@ToString
public class ExpirationDate {

    private final LocalDate value;

    public ExpirationDate(LocalDate value) {
        Objects.requireNonNull(value, "Expiration date cannot be null.");
        validateDate(value);
        this.value = value;
    }

    private void validateDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidCouponException("Expiration date cannot be in the past.");
        }
    }

    public boolean isExpired() {
        return value.isBefore(LocalDate.now());
    }
}
