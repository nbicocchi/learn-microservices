package com.nbicocchi.monolith.persistence.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.Version;

import java.util.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "productId")
    private Set<Recommendation> recommendations;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "productId")
    private Set<Review> reviews;

    @Version
    private Integer version;
    private String name;
    private int weight;

    public Product(){
    }

    public Product(String name, int weight, Integer version){
        this.recommendations = new HashSet<>();
        this.reviews = new HashSet<>();
        this.name = name;
        this.weight = weight;
    }

    public Product(Set<Recommendation> recommendations, Set<Review> reviews) {
        this.recommendations = recommendations;
        this.reviews = reviews;
        name = null;
        weight = 0;
    }

    public Product(Set<Recommendation> recommendations, Set<Review> reviews, String name, int weight) {
        this.recommendations = recommendations;
        this.reviews = reviews;
        this.name = name;
        this.weight = weight;
    }

    public Product(Set<Recommendation> recommendations, Set<Review> reviews, String name, int weight, Integer version) {
        this.recommendations = recommendations;
        this.reviews = reviews;
        this.name = name;
        this.weight = weight;
        this.version = version;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void addRecommendation(Recommendation r) {
        recommendations.add(r);
    }

    public void removeRecommendation(Recommendation r){
        recommendations.remove(r);
    }

    public void addReview(Review r) {
        reviews.add(r);
    }

    public void removeReview(Review r){
        reviews.remove(r);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", recommendations=" + recommendations +
                ", reviews=" + reviews +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }
}
