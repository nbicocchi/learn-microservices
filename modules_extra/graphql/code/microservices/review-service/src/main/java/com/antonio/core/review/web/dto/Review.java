package com.antonio.core.review.web.dto;

public class Review {
    private int productId;
    private int reviewId;
    private String author;
    private String subject;
    private String content;

    public Review() {
        productId = 0;
        reviewId = 0;
        author = null;
        subject = null;
        content = null;
    }

    public Review(
            int productId,
            int reviewId,
            String author,
            String subject,
            String content) {

        this.productId = productId;
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    public int getProductId() {
        return productId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }
}