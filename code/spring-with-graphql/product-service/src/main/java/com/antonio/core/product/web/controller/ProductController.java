package com.antonio.core.product.web.controller;

import com.antonio.core.product.web.dto.Product;
import com.antonio.core.product.web.dto.ProductAggregate;
import com.antonio.core.product.web.dto.ProductAggregateInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;

public interface ProductController {

    @QueryMapping
    public Product getProduct(@Argument int productId);

    @MutationMapping
    public Product createProduct(@Argument Product input);

    @MutationMapping
    public Boolean deleteProduct(@Argument int productId);

    @MutationMapping
    public ProductAggregate createProductAggregate(@Argument ProductAggregateInput input);

    @MutationMapping
    public Boolean deleteProductAggregate(@Argument int productId);

    @QueryMapping
    public ProductAggregate getProductAggregate(@Argument int productId);
}