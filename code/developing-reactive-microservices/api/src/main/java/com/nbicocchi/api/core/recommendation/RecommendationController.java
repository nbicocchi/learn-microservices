package com.nbicocchi.api.core.recommendation;

import java.util.List;
import org.springframework.web.bind.annotation.*;

public interface RecommendationController {

  /**
   * Sample usage, see below.
   *
   * curl -X POST $HOST:$PORT/recommendation \
   *   -H "Content-Type: application/json" --data \
   *   '{"productId":123,"recommendationId":456,"author":"me","rate":5,"content":"yada, yada, yada"}'
   *
   * @param body A JSON representation of the new recommendation
   * @return A JSON representation of the newly created recommendation
   */
  @PostMapping(
    value    = "/recommendation",
    consumes = "application/json",
    produces = "application/json")
  RecommendationDto createRecommendation(@RequestBody RecommendationDto body);

  /**
   * Sample usage: "curl $HOST:$PORT/recommendation?productId=1".
   *
   * @param productId Id of the product
   * @return the recommendations of the product
   */
  @GetMapping(
    value = "/recommendation",
    produces = "application/json")
  List<RecommendationDto> getRecommendations(
    @RequestParam(value = "productId", required = true) int productId);

  /**
   * Sample usage: "curl -X DELETE $HOST:$PORT/recommendation?productId=1".
   *
   * @param productId Id of the product
   */
  @DeleteMapping(value = "/recommendation")
  void deleteRecommendations(@RequestParam(value = "productId", required = true)  int productId);
}
