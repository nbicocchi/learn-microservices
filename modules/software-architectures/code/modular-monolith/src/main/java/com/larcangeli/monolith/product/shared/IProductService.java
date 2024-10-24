package com.nbicocchi.monolith.product.shared;

import java.util.Collection;

public interface IProductService {
    ProductDTO findById(Long id);

    Collection<ProductDTO> findAll();

    ProductDTO save(ProductDTO product);

    void deleteById(Long id);
}
