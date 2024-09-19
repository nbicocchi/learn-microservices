package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.ProductPurchase;
import org.springframework.data.repository.CrudRepository;

public interface  ProductPurchaseRepository extends CrudRepository<ProductPurchase, Long> {
}
