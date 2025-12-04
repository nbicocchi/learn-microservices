package com.nbicocchi.monolith.adapters.api.controller.impl;

import com.nbicocchi.monolith.adapters.api.dto.ProductAggregateDTO;
import com.nbicocchi.monolith.adapters.api.dto.RecommendationDTO;
import com.nbicocchi.monolith.adapters.api.dto.ReviewDTO;
import com.nbicocchi.monolith.adapters.api.mapper.ProductAggregateMapper;
import com.nbicocchi.monolith.adapters.api.mapper.RecommendationMapper;
import com.nbicocchi.monolith.adapters.api.mapper.ReviewMapper;
import com.nbicocchi.monolith.adapters.api.controller.IThymeleafController;
import com.nbicocchi.monolith.adapters.api.util.exceptions.NotFoundException;
import com.nbicocchi.monolith.adapters.api.util.exceptions.UnprocessableEntityException;
import com.nbicocchi.monolith.core.usecase.creation.CreationInputBoundary;
import com.nbicocchi.monolith.core.usecase.removal.RemovalInputBoundary;
import com.nbicocchi.monolith.core.usecase.retrieval.RetrievalInputBoundary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class ThymeleafController implements IThymeleafController {

    private static final Logger LOG = LoggerFactory.getLogger(com.nbicocchi.monolith.adapters.api.controller.impl.ThymeleafController.class);
    private final RetrievalInputBoundary retrievalInputBoundary;
    private final ProductAggregateMapper productMapper;
    private final CreationInputBoundary creationInputBoundary;
    private final RemovalInputBoundary removalInputBoundary;
    private final RecommendationMapper recommendationMapper;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ThymeleafController(RetrievalInputBoundary retrievalInputBoundary, ProductAggregateMapper productMapper, CreationInputBoundary creationInputBoundary, RemovalInputBoundary removalInputBoundary, RecommendationMapper recommendationMapper, ReviewMapper reviewMapper) {
        this.retrievalInputBoundary = retrievalInputBoundary;
        this.productMapper = productMapper;
        this.creationInputBoundary = creationInputBoundary;
        this.removalInputBoundary = removalInputBoundary;
        this.recommendationMapper = recommendationMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public String homePage(Model model) {
        model.addAttribute("message", "Hello from Thymeleaf!");
        return "home";
    }

    @Override
    public String getAllProducts(Model model) {
        List<ProductAggregateDTO> products = retrievalInputBoundary.getAllProducts().stream().map(productMapper::entityToDto).toList();
        LOG.debug("all products: {}", products);
        model.addAttribute("allProducts", products);
        return "products";
    }

    @Override
    public String getProduct(Model model, @PathVariable Long productId){
        if(productId < 0){
            throw new UnprocessableEntityException("Invalid productId: " + productId);
        }
        LOG.debug("getProduct: lookup a product aggregate for productId: {}", productId);
        try{
            ProductAggregateDTO product = productMapper.entityToDto(retrievalInputBoundary.getProduct(productId));
            model.addAttribute("product", product);
            return "product_item";
        }catch (NotFoundException e){
            throw new NotFoundException("No product found with ID: " + productId);
        }
    }

    @Override
    public String createProduct(Model model, @ModelAttribute("product") ProductAggregateDTO request){
        try{
            LOG.debug("createProduct: creates a new composite entity for productId: {}", request.productId());
            ProductAggregateDTO product = productMapper.entityToDto(creationInputBoundary.createProduct(productMapper.dtoToEntity(request)));
            LOG.debug("createProduct: product {} created", product.productId());
        }catch (RuntimeException re) {
            LOG.warn("createProduct failed", re);
            throw re;
        }
        // return updated products list
        List<ProductAggregateDTO> products = retrievalInputBoundary.getAllProducts().stream().map(productMapper::entityToDto).toList();
        model.addAttribute("allProducts", products);
        return "products";
    }

    @Override
    public String deleteProduct(Model model, @PathVariable Long productId){
        LOG.debug("deleteProduct: Deletes a product aggregate for productId: {}", productId);
        try{
            removalInputBoundary.deleteProduct(productId);
            LOG.debug("deleteProduct: aggregate entities deleted for productId: {}", productId);
            // redirect to products list
            return "redirect:/products";
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Product with ID: " + productId + " not found");
        }
    }

    @Override
    public String createRecommendation(Model model, @PathVariable Long productId, @ModelAttribute("recommendation") RecommendationDTO recommendation){
        try{
            LOG.debug("createRecommendation: creates a new recommendation entity for productId: {}", productId);
            creationInputBoundary.createRecommendation(recommendationMapper.dtoToEntity(recommendation));
            LOG.debug("createRecommendation: recommendation created");
        }catch (RuntimeException re) {
            LOG.warn("createRecommendation failed", re);
            throw re;
        }
        // redirect to product page
        return ("redirect:/products/" + productId);
    }

    @Override
    public String deleteRecommendation(Model model, @PathVariable Long productId, @PathVariable Long recommendationId){
        LOG.debug("deleteRecommendation: Deletes the recommendation with ID: {}", recommendationId);
        try{
            removalInputBoundary.deleteRecommendation(productId, recommendationId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Recommendation with ID: " + recommendationId + " not found");
        }
        LOG.debug("deleteRecommendation: recommendation deleted for ID: {}", recommendationId);
        // redirect to product page
        return ("redirect:/products/" + productId);
    }

    @Override
    public String createReview(Model model, @PathVariable Long productId, @ModelAttribute("review") ReviewDTO review){
        try{
            LOG.debug("createReview: creates a new review entity for productId: {}", productId);
            creationInputBoundary.createReview(reviewMapper.dtoToEntity(review));
            LOG.debug("createReview: review created");
        }catch (RuntimeException re) {
            LOG.warn("createReview failed", re);
            throw re;
        }
        // redirect to product page
        return ("redirect:/products/" + productId);
    }

    @Override
    public String deleteReview(Model model, @PathVariable Long productId, @PathVariable Long reviewId){
        LOG.debug("deleteReview: Deletes the review with ID: {}", reviewId);
        try{
            removalInputBoundary.deleteReview(productId, reviewId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Review with ID: " + reviewId + " not found");
        }
        LOG.debug("deleteReview: review deleted for ID: {}", reviewId);
        // redirect to product page
        return ("redirect:/products/" + productId);
    }
}
