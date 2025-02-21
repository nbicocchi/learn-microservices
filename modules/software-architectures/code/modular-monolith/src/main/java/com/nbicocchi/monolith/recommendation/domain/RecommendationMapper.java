package com.nbicocchi.monolith.recommendation.domain;

import com.nbicocchi.monolith.recommendation.shared.RecommendationDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
interface RecommendationMapper {
    RecommendationDTO toDTO(Recommendation r);
    List<RecommendationDTO> toDTOs(List<Recommendation> rs);
    Recommendation toEntity(RecommendationDTO rDTO);
    List<Recommendation> toEntities(List<RecommendationDTO> rDTOs);
}
