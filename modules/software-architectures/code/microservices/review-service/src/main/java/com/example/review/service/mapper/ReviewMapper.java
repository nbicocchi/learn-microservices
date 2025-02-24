package com.example.review.service.mapper;

import com.example.review.service.persistence.model.Review;
import com.example.review.service.web.dto.ReviewDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDTO toDTO(Review r);
    List<ReviewDTO> toDTOs(List<Review> rs);
    Review toEntity(ReviewDTO rDTO);
    List<Review> toEntities(List<ReviewDTO> rDTOs);
}
