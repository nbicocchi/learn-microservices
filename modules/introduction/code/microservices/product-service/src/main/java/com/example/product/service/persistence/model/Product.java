package com.example.product.service.persistence.model;

import com.example.product.service.web.dto.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.util.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private int weight;

    public Product(){
    }

    @Transient
    private Set<ReviewDTO> reviews; // Dati esterni da Review Service

    @Transient
    private Set<RecommendationDTO> recommendations; // Dati esterni da Recommendation Service

    public Product(String name, int weight){
        this.name = name;
        this.weight = weight;
        this.recommendations = new HashSet<>();
        this.reviews = new HashSet<>();
    }

    public Product(Set<RecommendationDTO> recommendations, Set<ReviewDTO> reviews) {
        this.recommendations = recommendations;
        this.reviews = reviews;
        name = null;
        weight = 0;
    }

    public Product(Set<RecommendationDTO> recommendations, Set<ReviewDTO> reviews, String name, int weight) {
        this.recommendations = recommendations;
        this.reviews = reviews;
        this.name = name;
        this.weight = weight;
    }

    public Product(Set<RecommendationDTO> recommendations, Set<ReviewDTO> reviews, String name, int weight, Integer version) {
        this.recommendations = recommendations;
        this.reviews = reviews;
        this.name = name;
        this.weight = weight;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    public Set<ReviewDTO> getReviews() {
        return reviews;
    }

    public Set<RecommendationDTO> getRecommendations() {
        return recommendations;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", recommendations=" + recommendations +
                ", reviews=" + reviews +
                '}';
    }
}