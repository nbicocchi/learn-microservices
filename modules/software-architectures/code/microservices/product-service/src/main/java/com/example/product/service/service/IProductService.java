package com.example.product.service.service;
import com.example.product.service.web.dto.ProductDTO;

import java.util.Collection;

public interface IProductService {
    ProductDTO findById(Long id);

    Collection<ProductDTO> findAll();

    ProductDTO save(ProductDTO product);

    void deleteById(Long id);
}