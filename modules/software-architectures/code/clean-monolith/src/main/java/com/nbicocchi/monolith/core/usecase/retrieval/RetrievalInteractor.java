package com.nbicocchi.monolith.core.usecase.retrieval;

import com.nbicocchi.monolith.core.entity.product.IProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A simple use case that implements the logic of retrieving entities
 */
@Component
public class RetrievalInteractor implements RetrievalInputBoundary {

    RetrievalOutputBoundary retrievalOutputBoundary;

    public RetrievalInteractor(RetrievalOutputBoundary retrievalOutputBoundary) {
        this.retrievalOutputBoundary = retrievalOutputBoundary;
    }

    @Override
    public IProductEntity getProduct(Long productId) {
        return retrievalOutputBoundary.getProduct(productId);
    }

    @Override
    public List<IProductEntity> getAllProducts() {
        return retrievalOutputBoundary.getAllProducts();
    }
}
