package com.larcangeli.monolith.adapters.api.mapper;
import com.larcangeli.monolith.adapters.persistence.implementation.Review;
import com.larcangeli.monolith.core.entity.review.impl.ReviewEntity;
import com.larcangeli.monolith.core.entity.review.IReviewEntity;
import com.larcangeli.monolith.adapters.api.dto.ReviewDTO;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review entityToPersistence(IReviewEntity reviewEntity);
    ReviewEntity persistenceToEntity(Review review);
    Set<ReviewEntity> persistenceToEntities(Set<Review> reviews);
    Set<Review> entitiesToPersistence(Set<IReviewEntity> reviewEntities);
    ReviewDTO entityToDto(IReviewEntity reviewEntity);
    ReviewEntity dtoToEntity(ReviewDTO review);

}
