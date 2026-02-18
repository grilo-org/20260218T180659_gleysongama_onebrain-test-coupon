package br.com.gama.cupon.domain.model;

import br.com.gama.cupon.domain.exception.InvalidCouponException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
@ToString
public class CouponCode {

    private static final int CODE_LENGTH = 6;
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    private final String value;

    public CouponCode(String code) {
        Objects.requireNonNull(code, "Coupon code cannot be null.");
        String sanitizedCode = sanitizeCode(code);
        validateCode(sanitizedCode);
        this.value = sanitizedCode;
    }

    private String sanitizeCode(String code) {
        String alphanumericOnly = code.replaceAll("[^a-zA-Z0-9]", "");
        if (alphanumericOnly.length() > CODE_LENGTH) {
            return alphanumericOnly.substring(0, CODE_LENGTH);
        } else if (alphanumericOnly.length() < CODE_LENGTH) {
            return String.format("%-" + CODE_LENGTH + "s", alphanumericOnly).replace(' ', 'X');
        }
        return alphanumericOnly;
    }

    private void validateCode(String code) {
        if (code.length() != CODE_LENGTH) {
            throw new InvalidCouponException("Coupon code must have exactly " + CODE_LENGTH + " alphanumeric characters after sanitization.");
        }
        if (!ALPHANUMERIC_PATTERN.matcher(code).matches()) {
            throw new InvalidCouponException("Coupon code must contain only alphanumeric characters.");
        }
    }
}
