package com.example.review.service.service.impl;

import com.example.review.service.mapper.ReviewMapper;
import com.example.review.service.persistence.model.Review;
import com.example.review.service.persistence.repository.ReviewRepository;
import com.example.review.service.service.IReviewService;
import com.example.review.service.web.dto.ReviewDTO;
import com.example.review.service.web.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService implements IReviewService {

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
        Optional<Review> p = repo.findById(reviewId);
        if(p.isEmpty())
            throw new NotFoundException();
        try{
            repo.deleteReviewById(reviewId);
            repo.flush();
        }catch (NotFoundException e){
            throw new NotFoundException("No review found with ID: " + reviewId);
        }
    }

    @Override
    public void deleteReviews(Long productId) {
        try{
            repo.deleteAllReviewsByProductId(productId);
        }catch (NotFoundException e){
            throw new NotFoundException("No product found with ID: " + productId);
        }
    }

    @Override
    public List<ReviewDTO> findReviewsByProductId(Long productId) {
        try{
            return mapper.toDTOs(repo.findByProductId(productId));
        }catch (NotFoundException e){
            throw new NotFoundException("No product found with ID: " + productId);
        }
    }
}