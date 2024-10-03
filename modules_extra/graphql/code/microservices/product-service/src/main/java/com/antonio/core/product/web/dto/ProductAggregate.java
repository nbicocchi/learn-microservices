package com.antonio.core.product.web.dto;

import java.util.List;

public class ProductAggregate {
    private final int productId;
    private final String name;
    private final int weight;
    private final List<ReviewSummary> reviews;

    public ProductAggregate() {
        productId = 0;
        name = null;
        weight = 0;
        reviews = null;
    }

    public ProductAggregate(
            int productId,
            String name,
            int weight,
            List<ReviewSummary> reviews) {

        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.reviews = reviews;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public List<ReviewSummary> getReviews() {
        return reviews;
    }
}