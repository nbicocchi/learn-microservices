package com.nbicocchi.monolith.mapper;

import com.nbicocchi.monolith.persistence.model.Recommendation;
import com.nbicocchi.monolith.web.dto.RecommendationDTO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    RecommendationDTO recommendationToRecommendationDTO(Recommendation r);
    List<RecommendationDTO> recommendationsToRecommendationDTOs(Set<Recommendation> rs);
    Recommendation recommendationDTOToRecommendation(RecommendationDTO rDTO);
    Set<Recommendation> recommendationDTOsToRecommendations(List<RecommendationDTO> rDTOs);
}
