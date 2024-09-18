package com.larcangeli.monolith.mapper;

import com.larcangeli.monolith.persistence.model.Review;
import com.larcangeli.monolith.web.dto.ReviewDTO;
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
