package com.antonio.core.review.controller;

import com.antonio.core.review.web.dto.Review;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import java.util.List;

public interface ReviewController {

    @QueryMapping
    public List<Review> getReviews(@Argument int productId);

    @MutationMapping
    public Review createReviews(@Argument Review input);

    @MutationMapping
    public Boolean deleteReviews(@Argument int productId);
}