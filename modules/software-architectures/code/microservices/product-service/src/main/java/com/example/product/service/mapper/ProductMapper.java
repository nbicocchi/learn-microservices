package com.example.product.service.mapper;

import com.example.product.service.persistence.model.Product;
import com.example.product.service.web.dto.ProductDTO;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product p);
    Product toEntity(ProductDTO pDTO);
}
