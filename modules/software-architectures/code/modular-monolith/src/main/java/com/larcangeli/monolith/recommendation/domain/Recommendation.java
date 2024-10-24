package com.nbicocchi.monolith.recommendation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.Version;

import java.util.Objects;

@Entity
class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendationId;

    private Long productId;

    @Version
    private Integer version;

    private String author;
    private int rating;
    private String content;

    public Recommendation() {
    }

    public Recommendation(Long productId, String author, int rating, String content) {
        this.productId = productId;
        this.author = author;
        this.rating = rating;
        this.content = content;
    }

    public Long getRecommendationId() {
        return recommendationId;
    }

    public Integer getVersion() {
        return version;
    }

    public Long getProductId() {
        return productId;
    }

    public String getAuthor() {
        return author;
    }

    public int getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public void setRecommendationId(Long recommendationId) {
        this.recommendationId = recommendationId;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recommendation that = (Recommendation) o;
        return Objects.equals(recommendationId, that.recommendationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recommendationId);
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "recommendationId=" + recommendationId +
                ", productId=" + productId +
                ", version=" + version +
                ", author='" + author + '\'' +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                '}';
    }
}
