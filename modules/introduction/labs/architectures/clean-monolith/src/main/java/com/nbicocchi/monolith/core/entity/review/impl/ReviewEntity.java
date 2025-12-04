package com.nbicocchi.monolith.core.entity.review.impl;

import com.nbicocchi.monolith.core.entity.review.IReviewEntity;

public class ReviewEntity implements IReviewEntity {
    Long reviewId;
    Long productId;
    String author;
    String subject;
    String content;

    public ReviewEntity(){

    }

    public ReviewEntity(String author, String subject, String content) {
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    public ReviewEntity(Long productId, String author, String subject, String content) {
        this.productId = productId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    public ReviewEntity(Long reviewId, Long productId, String author, String subject, String content) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public Long getProductId() {
        return productId;
    }

    @Override
    public Long getReviewId() {
        return this.reviewId;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public String getSubject() {
        return this.subject;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    @Override
    public String toString() {
        return "ReviewEntity{" +
                "reviewId=" + reviewId +
                ", productId=" + productId +
                ", author='" + author + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

