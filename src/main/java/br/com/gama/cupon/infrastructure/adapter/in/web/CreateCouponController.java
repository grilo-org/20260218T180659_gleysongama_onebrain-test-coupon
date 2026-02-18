package br.com.gama.cupon.infrastructure.adapter.in.web;

import br.com.gama.cupon.application.port.in.CreateCouponUseCase;
import br.com.gama.cupon.infrastructure.adapter.in.web.dto.CouponRequestDTO;
import br.com.gama.cupon.infrastructure.adapter.in.web.dto.CouponResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateCouponController extends CouponAbstractController{

    private final CreateCouponUseCase createCouponUseCase;

    @Operation(summary = "Cria um novo cupom de desconto", description = "Cadastra um cupom com código, descrição, valor de desconto e data de expiração.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping
    public ResponseEntity<CouponResponseDTO> createCoupon(@Valid @RequestBody CouponRequestDTO requestDTO) {
        var command = requestDTO.toCreateCommand();
        var response = createCouponUseCase.createCoupon(command);
        return new ResponseEntity<>(CouponResponseDTO.fromApplicationResponse(response), HttpStatus.CREATED);
    }
}
