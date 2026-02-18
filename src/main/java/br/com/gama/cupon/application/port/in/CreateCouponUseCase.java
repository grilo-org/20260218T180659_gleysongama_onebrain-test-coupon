package br.com.gama.cupon.application.port.in;

import br.com.gama.cupon.application.command.CreateCouponCommand;
import br.com.gama.cupon.application.query.CouponResponse;

public interface CreateCouponUseCase {
    CouponResponse createCoupon(CreateCouponCommand command);
}
