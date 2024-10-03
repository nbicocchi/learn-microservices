package com.antonio.core.review.controller;

import com.antonio.core.review.persistence.ReviewEntity;
import com.antonio.core.review.persistence.ReviewRepository;
import com.antonio.core.review.mapper.ReviewMapper;
import com.antonio.core.review.services.ReviewServiceImpl;
import com.antonio.core.review.web.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import com.antonio.core.review.web.dto.Review;
import com.antonio.core.review.web.exceptions.InvalidInputException;
import com.antonio.core.review.web.errors.ServiceUtil;

import java.util.List;

@Controller
public class ReviewControllerImpl implements ReviewController {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository repository;

    private final ReviewMapper mapper;

    private final ServiceUtil serviceUtil;

    @Autowired
    public ReviewControllerImpl(ReviewRepository repository, ReviewMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Review createReviews(Review input) {
        try {
            ReviewEntity entity = mapper.apiToEntity(input);
            ReviewEntity newEntity = repository.save(entity);

            LOG.debug("createReview: created a review entity: {}/{}", input.getProductId(), input.getReviewId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " + input.getProductId() + ", Review Id:" + input.getReviewId());
        }
    }

    @Override
    public List<Review> getReviews(int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> list = mapper.entityListToApiList(entityList);

        if (list.size() == 0) {
            LOG.debug("getReviews: no reviews found for productId: {}", productId);
            throw new NotFoundException("No reviews found for productId: " + productId);
        }
        LOG.debug("getReviews: response size: {}", list.size());

        return list;
    }

    @Override
    public Boolean deleteReviews(int productId) {
        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        if (repository.findByProductId(productId).isEmpty()) {
            throw new NotFoundException("No reviews found for productId: " + productId);
        }
        repository.deleteAll(repository.findByProductId(productId));

        return Boolean.TRUE;
    }
}