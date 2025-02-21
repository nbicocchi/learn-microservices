package com.nbicocchi.monolith.review.domain;

import com.nbicocchi.monolith.review.shared.IReviewService;
import com.nbicocchi.monolith.review.shared.ReviewDTO;
import com.nbicocchi.monolith.util.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
class ReviewService implements IReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository repo;
    private final ReviewMapper mapper;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.repo = reviewRepository;
        this.mapper = reviewMapper;
    }

    @Override
    public ReviewDTO save(ReviewDTO review) {
        try{
            return mapper.toDTO(repo.save(mapper.toEntity(review)));
        }catch (RuntimeException re){
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }

    }

    @Override
    public void saveReviewOnProductCreation(ReviewDTO review) {
        repo.save(mapper.toEntity(review));
    }

    @Override
    public void deleteById(Long reviewId) {
        try{
            repo.deleteById(reviewId);
        }catch (NotFoundException e){
            throw new NotFoundException("No review found with ID: " + reviewId);
        }
    }

    @Override
    public void deleteReviews(Long productId) {
        try{
            repo.deleteAll(repo.findRecommendationsByProductId(productId));
        }catch (NotFoundException e){
            throw new NotFoundException("No product found with ID: " + productId);
        }
    }

    @Override
    public List<ReviewDTO> findReviewsByProductId(Long productId) {
        try{
            return mapper.toDTOs(repo.findRecommendationsByProductId(productId));
        }catch (NotFoundException e){
            throw new NotFoundException("No product found with ID: " + productId);
        }
    }
}
