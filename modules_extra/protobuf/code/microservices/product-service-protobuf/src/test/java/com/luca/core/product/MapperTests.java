package com.luca.core.product;

import static org.junit.jupiter.api.Assertions.*;

import com.luca.core.product.persistence.ProductEntity;
import com.luca.core.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import com.luca.product.protobuf.ProductProto.Product;

class MapperTests {

  private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

  @Test
  void mapperTests() {

    assertNotNull(mapper);

    Product api = Product.newBuilder()
            .setProductId(1)
            .setName("n")
            .setWeight(1)
            .build();

    ProductEntity entity = mapper.apiToEntity(api);

    assertEquals(api.getProductId(), entity.getProductId());
    assertEquals(api.getProductId(), entity.getProductId());
    assertEquals(api.getName(), entity.getName());
    assertEquals(api.getWeight(), entity.getWeight());

    Product.Builder builder = mapper.entityToApi(entity).toBuilder();
    Product api2 = builder.build();
    assertEquals(api.getProductId(), api2.getProductId());
    assertEquals(api.getProductId(), api2.getProductId());
    assertEquals(api.getName(),      api2.getName());
    assertEquals(api.getWeight(),    api2.getWeight());
    assertEquals("", api2.getServiceAddress());

  }
}