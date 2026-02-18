package br.com.gama.cupon.application.port.in;

import java.util.UUID;

public interface DeleteCouponUseCase {
    void deleteCoupon(UUID id);
}
