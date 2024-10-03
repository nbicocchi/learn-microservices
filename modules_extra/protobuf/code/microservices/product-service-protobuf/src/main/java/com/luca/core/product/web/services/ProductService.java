package com.luca.core.product.web.services;

import org.springframework.web.bind.annotation.*;
import com.luca.product.protobuf.ProductProto.Product;

public interface ProductService {

  /**
   * Sample usage, see below.
   *
   * curl -X POST http://localhost:7001/product \
   *   -H "Content-Type: application/x-protobuf" \
   *   --data-binary @product.bin
   *
   * @param body A binary representation of the new product
   * @return A binary representation of the newly created product
   */
  @PostMapping(
    value    = "/product",
    consumes = "application/x-protobuf",
    produces = "application/x-protobuf")
  Product createProduct(@RequestBody Product body);

  /**
   * Sample usage: "curl $HOST:$PORT/product/1".
   *
   * @param productId Id of the product
   * @return A binary representation of the product, if found, else null
   */
  @GetMapping(
    value = "/product/{productId}",
    produces = "application/x-protobuf")
  Product getProduct(@PathVariable int productId);

  /**
   * Sample usage: "curl -X DELETE $HOST:$PORT/product/1".
   *
   * @param productId Id of the product
   */
  @DeleteMapping(value = "/product/{productId}")
  void deleteProduct(@PathVariable int productId);
}
