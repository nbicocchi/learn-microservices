package com.example.recommendation.service.mapper;

import com.example.recommendation.service.persistence.model.Recommendation;
import com.example.recommendation.service.web.dto.RecommendationDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    RecommendationDTO toDTO(Recommendation r);
    List<RecommendationDTO> toDTOs(List<Recommendation> rs);
    Recommendation toEntity(RecommendationDTO rDTO);
    List<Recommendation> toEntities(List<RecommendationDTO> rDTOs);
}
