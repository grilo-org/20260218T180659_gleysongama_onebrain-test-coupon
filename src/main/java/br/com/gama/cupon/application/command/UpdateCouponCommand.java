package br.com.gama.cupon.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UpdateCouponCommand {
    private String description;
    private BigDecimal discountValue;
    private LocalDate expirationDate;
    private boolean published;
}
