package br.com.gama.cupon.infrastructure.adapter.in.web;

import br.com.gama.cupon.application.port.in.DeleteCouponUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteCouponController extends CouponAbstractController{

    private final DeleteCouponUseCase deleteCouponUseCase;

    @Operation(summary = "Deleta um cupom por ID", description = "Realiza um soft delete de um cupom existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cupom deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado"),
            @ApiResponse(responseCode = "409", description = "Cupom já foi deletado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        deleteCouponUseCase.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
