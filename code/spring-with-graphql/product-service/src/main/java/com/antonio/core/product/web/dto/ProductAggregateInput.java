package com.antonio.core.product.web.dto;

public class ProductAggregateInput {
    private int productId;
    private String name;
    private int weight;
    private int reviewId;
    private String author;
    private String subject;
    private String content;

    public ProductAggregateInput() {
        productId = 0;
        name = null;
        weight = 0;
        reviewId = 0;
        author = null;
        subject = null;
        content = null;
    }

    public ProductAggregateInput(
            int productId,
            String name,
            int weight,
            int reviewId,
            String author,
            String subject,
            String content) {

        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}