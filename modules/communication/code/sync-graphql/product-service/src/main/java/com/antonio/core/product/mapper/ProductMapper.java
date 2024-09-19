package com.antonio.core.product.mapper;

import com.antonio.core.product.persistence.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.antonio.core.product.web.dto.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  Product entityToApi(ProductEntity entity);

  @Mappings({
    @Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)
  })
  ProductEntity apiToEntity(Product api);
}
