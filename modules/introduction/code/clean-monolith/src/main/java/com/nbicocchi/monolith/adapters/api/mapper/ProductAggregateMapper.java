package com.nbicocchi.monolith.adapters.api.mapper;

import com.nbicocchi.monolith.adapters.persistence.implementation.Product;
import com.nbicocchi.monolith.core.entity.product.impl.ProductEntity;
import com.nbicocchi.monolith.core.entity.product.IProductEntity;
import com.nbicocchi.monolith.adapters.api.dto.ProductAggregateDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductAggregateMapper {

    Product entityToPersistence(IProductEntity productEntity);
    ProductEntity persistenceToEntity(Product product);
    List<ProductEntity> persistenceToEntities(List<Product> products);
    List<Product> entitiesToPersistence(List<IProductEntity> productEntities);
    ProductAggregateDTO entityToDto(IProductEntity productEntities);
    ProductEntity dtoToEntity(ProductAggregateDTO product);
}
