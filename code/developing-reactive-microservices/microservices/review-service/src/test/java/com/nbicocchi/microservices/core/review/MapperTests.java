package com.nbicocchi.microservices.core.review;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import com.nbicocchi.api.core.review.ReviewDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import com.nbicocchi.microservices.core.review.persistence.ReviewEntity;
import com.nbicocchi.microservices.core.review.controller.ReviewMapper;


class MapperTests {

  private ReviewMapper mapper = Mappers.getMapper(ReviewMapper.class);

  @Test
  void mapperTests() {

    assertNotNull(mapper);

    ReviewDto api = new ReviewDto(1, 2, "a", "s", "C", "adr");

    ReviewEntity entity = mapper.apiToEntity(api);

    assertEquals(api.getProductId(), entity.getProductId());
    assertEquals(api.getReviewId(), entity.getReviewId());
    assertEquals(api.getAuthor(), entity.getAuthor());
    assertEquals(api.getSubject(), entity.getSubject());
    assertEquals(api.getContent(), entity.getContent());

    ReviewDto api2 = mapper.entityToApi(entity);

    assertEquals(api.getProductId(), api2.getProductId());
    assertEquals(api.getReviewId(), api2.getReviewId());
    assertEquals(api.getAuthor(), api2.getAuthor());
    assertEquals(api.getSubject(), api2.getSubject());
    assertEquals(api.getContent(), api2.getContent());
    assertNull(api2.getServiceAddress());
  }

  @Test
  void mapperListTests() {

    assertNotNull(mapper);

    ReviewDto api = new ReviewDto(1, 2, "a", "s", "C", "adr");
    List<ReviewDto> apiList = Collections.singletonList(api);

    List<ReviewEntity> entityList = mapper.apiListToEntityList(apiList);
    assertEquals(apiList.size(), entityList.size());

    ReviewEntity entity = entityList.get(0);

    assertEquals(api.getProductId(), entity.getProductId());
    assertEquals(api.getReviewId(), entity.getReviewId());
    assertEquals(api.getAuthor(), entity.getAuthor());
    assertEquals(api.getSubject(), entity.getSubject());
    assertEquals(api.getContent(), entity.getContent());

    List<ReviewDto> api2List = mapper.entityListToApiList(entityList);
    assertEquals(apiList.size(), api2List.size());

    ReviewDto api2 = api2List.get(0);

    assertEquals(api.getProductId(), api2.getProductId());
    assertEquals(api.getReviewId(), api2.getReviewId());
    assertEquals(api.getAuthor(), api2.getAuthor());
    assertEquals(api.getSubject(), api2.getSubject());
    assertEquals(api.getContent(), api2.getContent());
    assertNull(api2.getServiceAddress());
  }
}
