package com.nbicocchi.monolith.mapper;

import com.nbicocchi.monolith.persistence.model.Product;
import com.nbicocchi.monolith.web.dto.ProductAggregateDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecommendationMapper.class, ReviewMapper.class})
public interface ProductAggregateMapper {
    ProductAggregateDTO productAggregateToProductAggregateDTO(Product p);
    Product productAggregateDTOToProductAggregate(ProductAggregateDTO pDTO);
}
