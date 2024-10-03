package com.luca.core.product.mapper;

import com.luca.core.product.persistence.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.luca.product.protobuf.ProductProto.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mappings({
          @Mapping(target = "serviceAddress", ignore = true)
  })
  Product entityToApi(ProductEntity entity);

  @Mappings({
          @Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)
  })
  ProductEntity apiToEntity(Product api);
}