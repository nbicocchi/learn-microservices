package com.nbicocchi.monolith.product.domain;

import com.nbicocchi.monolith.product.shared.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface ProductMapper {
    ProductDTO toDTO(Product p);
    Product toEntity(ProductDTO pDTO);
}
