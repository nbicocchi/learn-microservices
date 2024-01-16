package com.nbicocchi.api.composite.product;

public class ReviewSummaryDto {

  private final int reviewId;
  private final String author;
  private final String subject;
  private final String content;

  public ReviewSummaryDto() {
    this.reviewId = 0;
    this.author = null;
    this.subject = null;
    this.content = null;
  }

  public ReviewSummaryDto(int reviewId, String author, String subject, String content) {
    this.reviewId = reviewId;
    this.author = author;
    this.subject = subject;
    this.content = content;
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
}
