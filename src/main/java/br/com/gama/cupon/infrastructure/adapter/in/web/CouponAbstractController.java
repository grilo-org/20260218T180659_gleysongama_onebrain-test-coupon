package br.com.gama.cupon.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/coupons")
@Tag(name = "Coupons", description = "API para gerenciamento de cupons de desconto")
public abstract class CouponAbstractController {

}
