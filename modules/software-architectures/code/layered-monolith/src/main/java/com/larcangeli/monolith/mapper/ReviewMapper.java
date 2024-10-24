package com.nbicocchi.monolith.mapper;

import com.nbicocchi.monolith.persistence.model.Review;
import com.nbicocchi.monolith.web.dto.ReviewDTO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDTO reviewToReviewDTO(Review r);
    List<ReviewDTO> reviewsToReviewDTOs(Set<Review> rs);
    Review reviewDTOToReview(ReviewDTO rDTO);
    Set<Review> reviewDTOsToReviews(List<ReviewDTO> rDTOs);
}
