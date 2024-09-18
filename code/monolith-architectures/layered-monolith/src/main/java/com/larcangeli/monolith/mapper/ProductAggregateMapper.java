package com.larcangeli.monolith.mapper;

import com.larcangeli.monolith.persistence.model.Product;
import com.larcangeli.monolith.web.dto.ProductAggregateDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RecommendationMapper.class, ReviewMapper.class})
public interface ProductAggregateMapper {
    ProductAggregateDTO productAggregateToProductAggregateDTO(Product p);
    Product productAggregateDTOToProductAggregate(ProductAggregateDTO pDTO);
}
