package com.nbicocchi.monolith.product.web.controllers;

import com.nbicocchi.monolith.product.shared.ProductDTO;
import com.nbicocchi.monolith.product.shared.IProductService;
import com.nbicocchi.monolith.recommendation.shared.IRecommendationService;
import com.nbicocchi.monolith.recommendation.shared.RecommendationDTO;
import com.nbicocchi.monolith.review.shared.IReviewService;
import com.nbicocchi.monolith.review.shared.ReviewDTO;
import com.nbicocchi.monolith.util.exceptions.NotFoundException;
import com.nbicocchi.monolith.util.exceptions.UnprocessableEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
class ProductController implements IProductController{

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private final IProductService productService;
    private final IRecommendationService recommendationService;
    private final IReviewService reviewService;
    private final ApplicationEventPublisher events;

    @Autowired
    public ProductController(IProductService productService, IRecommendationService recommendationService, IReviewService reviewService, ApplicationEventPublisher events) {
        this.productService = productService;
        this.recommendationService = recommendationService;
        this.reviewService = reviewService;
        this.events = events;
    }

    @Override
    public String homePage(Model model) {
        model.addAttribute("message", "Hello from Thymeleaf!");
        return "home";
    }

    @Override
    public String getAllProducts(Model model) {
        List<ProductDTO> products = productService.findAll().stream().map(p -> getProduct(p.productId())).collect(Collectors.toList());
        LOG.debug("all products: {}", products);
        model.addAttribute("allProducts", products);
        return "products";
    }

    public ProductDTO getProduct(Long productId) {
        ProductDTO p = productService.findById(productId);

        List<RecommendationDTO> recommendations = recommendationService.findRecommendationsByProductId(productId);
        List<ReviewDTO> reviews = reviewService.findReviewsByProductId(productId);

        return createProductAggregate(p,recommendations,reviews);
    }

    @Override
    public String getProduct(Model model, @PathVariable Long productId){
        if(productId < 0){
            throw new UnprocessableEntityException("Invalid productId: " + productId);
        }
        LOG.debug("getProduct: lookup a product aggregate for productId: {}", productId);
        try{
            ProductDTO p = productService.findById(productId);

            List<RecommendationDTO> recommendations = recommendationService.findRecommendationsByProductId(productId);
            List<ReviewDTO> reviews = reviewService.findReviewsByProductId(productId);

            ProductDTO product = createProductAggregate(p,recommendations,reviews);

            model.addAttribute("product", product);
            return "product_item";
        }catch (NotFoundException e){
            throw new NotFoundException("No product found with ID: " + productId);
        }
    }

    @Override
    public String createProduct(Model model, @ModelAttribute("product") ProductDTO request){
        try{
            ProductDTO p = productService.save(request);
            if(!(request.recommendations() == null)){
                request.recommendations().forEach(r -> {
                    RecommendationDTO rec = new RecommendationDTO(r.recommendationId(),p.productId(),r.version(),r.author(),r.rating(),r.content());
                    events.publishEvent(rec);
                });
            }
            if(!(request.reviews()== null)){
                request.reviews().forEach(r -> {
                    ReviewDTO rev = new ReviewDTO(r.reviewId(),p.productId(),r.author(),r.subject(),r.content());
                    events.publishEvent(rev);
                });
            }
        }catch (RuntimeException re) {
            LOG.warn("createProduct failed", re);
            throw re;
        }
        // return updated products list
        List<ProductDTO> products = productService.findAll().stream().map(p -> getProduct(p.productId())).collect(Collectors.toList());
        LOG.debug("all products: {}", products);
        model.addAttribute("allProducts", products);
        return "products";
    }

    @Override
    public String deleteProduct(Model model, @PathVariable Long productId){
        LOG.debug("deleteProduct: Deletes a product aggregate for productId: {}", productId);
        try{
            productService.deleteById(productId);
            LOG.debug("deleteProduct: aggregate entities deleted for productId: {}", productId);
            // redirect to products list
            return "redirect:/products";
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Product with ID: " + productId + " not found");
        }
    }

    private ProductDTO createProductAggregate(
            ProductDTO p,
            List<RecommendationDTO> recommendations,
            List<ReviewDTO> reviews){

        // 1. Setup product info
        Long productId = p.productId();
        String name = p.name();
        int weight = p.weight();
        Integer version = p.version();

        // 2. Copy summary recommendation info, if available
        return new ProductDTO(productId, version, name, weight, recommendations, reviews);
    }
}

