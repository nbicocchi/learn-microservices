package com.larcangeli.monolith.web.controller;

import com.larcangeli.monolith.web.dto.ProductAggregateDTO;
import com.larcangeli.monolith.web.dto.RecommendationDTO;
import com.larcangeli.monolith.web.dto.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IProductCompositeController {

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
    ProductAggregateDTO getProduct(@PathVariable Long productId);

    @GetMapping(value = "/product-composite", produces = "application/json")
    List<ProductAggregateDTO> getAllProducts();

    @PostMapping(value    = "/product-composite", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ProductAggregateDTO createProduct(@RequestBody ProductAggregateDTO body);

    @DeleteMapping(value = "/product-composite/{productId}")
    void deleteProduct(@PathVariable Long productId);

    @PostMapping(value = "/product-composite/{productId}/recommendations", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    RecommendationDTO createRecommendation(@PathVariable Long productId, @RequestBody RecommendationDTO recommendation);

    @DeleteMapping(value = "/product-composite/{productId}/recommendations/{recommendationId}")
    void deleteRecommendation(@PathVariable Long productId, @PathVariable Long recommendationId);

    @PostMapping(value = "/product-composite/{productId}/reviews", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ReviewDTO createReview(@PathVariable Long productId, @RequestBody ReviewDTO review);

    @DeleteMapping(value = "/product-composite/{productId}/reviews/{reviewId}")
    void deleteReview(@PathVariable Long productId, @PathVariable Long reviewId);

}
