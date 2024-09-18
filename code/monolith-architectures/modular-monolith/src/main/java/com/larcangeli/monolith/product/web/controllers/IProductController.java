package com.larcangeli.monolith.product.web.controllers;

import com.larcangeli.monolith.product.shared.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IProductController {

    @Operation(
            summary = "${api.product-composite.get-composite-product.description}",
            description = "${api.product-composite.get-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @GetMapping(value = "/product-composite/{productId}", produces = "application/json")
    ProductDTO getProduct(@PathVariable Long productId);

    @GetMapping(value = "/product-composite", produces = "application/json")
    List<ProductDTO> getAllProducts();

    @PostMapping(value    = "/product-composite", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ProductDTO createProduct(@RequestBody ProductDTO body);

    @DeleteMapping(value = "/product-composite/{productId}")
    void deleteProduct(@PathVariable Long productId);

}
