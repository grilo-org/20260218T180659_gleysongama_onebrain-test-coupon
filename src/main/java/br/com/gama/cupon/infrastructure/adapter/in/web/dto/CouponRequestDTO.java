package br.com.gama.cupon.infrastructure.adapter.in.web.dto;

import br.com.gama.cupon.application.command.CreateCouponCommand;
import br.com.gama.cupon.application.command.UpdateCouponCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "DTO para requisição de criação ou atualização de cupom")
public class CouponRequestDTO {

    @Schema(description = "Código do cupom (6 caracteres alfanuméricos)", example = "CUPOM1")
    @NotBlank(message = "O código do cupom é obrigatório.")
    @Size(min = 6, max = 6, message = "O código do cupom deve ter 6 caracteres.") // Validação básica na camada web
    private String code;

    @Schema(description = "Descrição do cupom", example = "Desconto de 10% na primeira compra")
    @NotBlank(message = "A descrição do cupom é obrigatória.")
    private String description;

    @Schema(description = "Valor do desconto do cupom", example = "10.50")
    @NotNull(message = "O valor de desconto é obrigatório.")
    @DecimalMin(value = "0.5", message = "O valor de desconto deve ser no mínimo 0.5.")
    private BigDecimal discountValue;

    @Schema(description = "Data de expiração do cupom (formato YYYY-MM-DD)", example = "2024-12-31")
    @NotNull(message = "A data de expiração é obrigatória.")
    @FutureOrPresent(message = "A data de expiração não pode ser no passado.")
    private LocalDate expirationDate;

    @Schema(description = "Indica se o cupom está publicado e visível para uso", example = "true")
    private boolean published;

    public CreateCouponCommand toCreateCommand() {
        return CreateCouponCommand.builder()
                .code(this.code)
                .description(this.description)
                .discountValue(this.discountValue)
                .expirationDate(this.expirationDate)
                .published(this.published)
                .build();
    }

    public UpdateCouponCommand toUpdateCommand() {
        return UpdateCouponCommand.builder()
                .description(this.description)
                .discountValue(this.discountValue)
                .expirationDate(this.expirationDate)
                .published(this.published)
                .build();
    }
}
