package br.com.gama.cupon.infrastructure.adapter.in.web.dto;

import br.com.gama.cupon.application.query.CouponResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO para resposta de cupom")
public class CouponResponseDTO {
    @Schema(description = "ID único do cupom", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID id;
    @Schema(description = "Código do cupom", example = "CUPOM1")
    private String code;
    @Schema(description = "Descrição do cupom", example = "Desconto de 10% na primeira compra")
    private String description;
    @Schema(description = "Valor do desconto", example = "10.50")
    private BigDecimal discountValue;
    @Schema(description = "Data de expiração", example = "2024-12-31")
    private LocalDate expirationDate;
    @Schema(description = "Status de publicação", example = "true")
    private boolean published;
    @Schema(description = "Status de deleção (soft delete)", example = "false")
    private boolean deleted;
    @Schema(description = "Data de criação", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "Data da última atualização", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;

    public static CouponResponseDTO fromApplicationResponse(CouponResponse response) {
        return CouponResponseDTO.builder()
                .id(response.getId())
                .code(response.getCode())
                .description(response.getDescription())
                .discountValue(response.getDiscountValue())
                .expirationDate(response.getExpirationDate())
                .published(response.isPublished())
                .deleted(response.isDeleted())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }
}
