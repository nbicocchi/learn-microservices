package com.example.product.service.web.controller.impl;

import com.example.product.service.service.IProductService;
import com.example.product.service.web.controller.IProductController;
import com.example.product.service.web.dto.ProductDTO;
import com.example.product.service.web.dto.RecommendationDTO;
import com.example.product.service.web.dto.ReviewDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
class ProductController implements IProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private final IProductService productService;
    private final ApplicationEventPublisher events;

    @Autowired
    public ProductController(IProductService productService, ApplicationEventPublisher events) {
        this.productService = productService;
        this.events = events;
    }


    @Override
    public ProductDTO getProduct(@PathVariable Long productId){
        LOG.debug("getCompositeProduct: lookup a product aggregate for productId: {}", productId);
        return productService.findById(productId);
    }

    @Override
    public List<ProductDTO> getAllProducts(){
        return productService.findAll().stream().map(p -> getProduct(p.productId())).collect(Collectors.toList());
    }

    @Override
    public ProductDTO createProduct(@RequestBody ProductDTO body){
        LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.productId());
        ProductDTO p = productService.save(body);
        if(!(body.recommendations() == null)){
            body.recommendations().forEach(r -> {
                RecommendationDTO rec = new RecommendationDTO(r.recommendationId(),p.productId(),r.version(),r.author(),r.rating(),r.content());
                events.publishEvent(rec);
            });
        }
        if(!(body.reviews() == null)){
            body.reviews().forEach(r -> {
                ReviewDTO rev = new ReviewDTO(r.reviewId(),p.productId(),r.author(),r.subject(),r.content());
                events.publishEvent(rev);
            });
        }
        LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.productId());
        return p;
    }

    @Override
    public void deleteProduct(@PathVariable Long productId){
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);
        productService.deleteById(productId);
        events.publishEvent(productId);
        LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);
    }
}

