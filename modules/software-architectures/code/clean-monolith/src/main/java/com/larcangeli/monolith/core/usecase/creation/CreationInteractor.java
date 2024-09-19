package com.larcangeli.monolith.core.usecase.creation;

import com.larcangeli.monolith.core.entity.recommendation.impl.RecommendationEntity;
import com.larcangeli.monolith.core.entity.review.impl.ReviewEntity;
import com.larcangeli.monolith.core.entity.product.IProductFactory;
import com.larcangeli.monolith.core.entity.recommendation.IRecommendationFactory;
import com.larcangeli.monolith.core.entity.review.IReviewFactory;
import com.larcangeli.monolith.core.entity.product.IProductEntity;
import com.larcangeli.monolith.core.entity.recommendation.IRecommendationEntity;
import com.larcangeli.monolith.core.entity.review.IReviewEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple use case that implements the logic of saving entities
 */
@Component
public class CreationInteractor implements CreationInputBoundary {

    final IProductFactory productFactory;
    final IRecommendationFactory recommendationFactory;
    final IReviewFactory reviewFactory;
    final CreationOutputBoundary creationOutputBoundary;

    public CreationInteractor(IProductFactory productFactory,
                              IRecommendationFactory recommendationFactory,
                              IReviewFactory reviewFactory,
                              CreationOutputBoundary creationOutputBoundary) {
        this.productFactory = productFactory;
        this.recommendationFactory = recommendationFactory;
        this.reviewFactory = reviewFactory;
        this.creationOutputBoundary = creationOutputBoundary;
    }

    @Override
    public IProductEntity createProduct(IProductEntity p) {

        Set<RecommendationEntity> recommendationEntities = new HashSet<>();
        Set<ReviewEntity> reviewEntities = new HashSet<>();


        if(!p.getRecommendations().isEmpty()){
            p.getRecommendations().forEach(r -> {
                RecommendationEntity recommendationEntity = (RecommendationEntity) recommendationFactory
                        .createRecommendation(r.getVersion(),r.getAuthor(),r.getRating(),r.getContent());
                recommendationEntities.add(recommendationEntity);
            });
        }
        if(!p.getReviews().isEmpty()){
            p.getReviews().forEach(r -> {
                ReviewEntity reviewEntity = (ReviewEntity) reviewFactory
                        .createReview(r.getAuthor(),r.getSubject(),r.getContent());
                reviewEntities.add(reviewEntity);
            });
        }

        IProductEntity product = productFactory
                .createProduct(p.getProductId(), p.getVersion(), p.getName(), p.getWeight(), recommendationEntities, reviewEntities);
        return creationOutputBoundary.saveProduct(product);

    }

    @Override
    public IRecommendationEntity createRecommendation(IRecommendationEntity r) {
        return creationOutputBoundary.saveRecommendation(r);
    }

    @Override
    public IReviewEntity createReview(IReviewEntity r) {
        return creationOutputBoundary.saveReview(r);

    }
}
