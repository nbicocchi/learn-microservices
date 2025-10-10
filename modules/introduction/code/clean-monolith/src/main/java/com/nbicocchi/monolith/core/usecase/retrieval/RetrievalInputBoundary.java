package com.nbicocchi.monolith.core.usecase.retrieval;

import com.nbicocchi.monolith.core.entity.product.IProductEntity;

import java.util.List;

/**
 * A simple boundary that allows the Controller in the adapter layer to use all the underlying functions
 * without a direct interaction with the Use Case layer
 */
public interface RetrievalInputBoundary {
    IProductEntity getProduct(Long productId);

    List<IProductEntity> getAllProducts();
}
