package com.nbicocchi.microservices.core.recommendation.controller;

import java.util.List;

import com.nbicocchi.api.core.recommendation.RecommendationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.nbicocchi.microservices.core.recommendation.persistence.RecommendationEntity;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

  @Mappings({
    @Mapping(target = "rate", source = "entity.rating"),
    @Mapping(target = "serviceAddress", ignore = true)
  })
  RecommendationDto entityToApi(RecommendationEntity entity);

  @Mappings({
    @Mapping(target = "rating", source = "api.rate"),
    @Mapping(target = "id", ignore = true),
    @Mapping(target = "version", ignore = true)
  })
  RecommendationEntity apiToEntity(RecommendationDto api);

  List<RecommendationDto> entityListToApiList(List<RecommendationEntity> entity);

  List<RecommendationEntity> apiListToEntityList(List<RecommendationDto> api);
}