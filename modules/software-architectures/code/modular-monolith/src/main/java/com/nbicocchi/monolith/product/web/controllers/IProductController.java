package com.nbicocchi.monolith.product.web.controllers;

import com.nbicocchi.monolith.product.shared.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IProductController {

    @GetMapping("/")
    String homePage(Model model);

    @GetMapping("/products")
    String getAllProducts(Model model);

    @GetMapping(value = "/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    String getProduct(Model model, @PathVariable Long productId);

    @PostMapping(value = "/products")
    String createProduct(Model model, @ModelAttribute("product") ProductDTO request);

    @DeleteMapping(value = "/products/{productId}")
    String deleteProduct(Model model, @PathVariable Long productId);

}
