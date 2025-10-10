package com.nbicocchi.monolith.web.uicontroller.impl;

import com.nbicocchi.monolith.mapper.RecommendationMapper;
import com.nbicocchi.monolith.mapper.ReviewMapper;
import com.nbicocchi.monolith.persistence.model.Product;
import com.nbicocchi.monolith.persistence.model.Recommendation;
import com.nbicocchi.monolith.persistence.model.Review;
import com.nbicocchi.monolith.service.IProductCompositeService;
import com.nbicocchi.monolith.web.uicontroller.IThymeleafController;
import com.nbicocchi.monolith.web.dto.ProductAggregateDTO;
import com.nbicocchi.monolith.web.dto.RecommendationDTO;
import com.nbicocchi.monolith.web.dto.ReviewDTO;
import com.nbicocchi.monolith.web.exceptions.NotFoundException;
import com.nbicocchi.monolith.web.exceptions.UnprocessableEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ThymeleafController implements IThymeleafController {

    private static final Logger LOG = LoggerFactory.getLogger(ThymeleafController.class);

    private final RecommendationMapper recommendationMapper;
    private final ReviewMapper reviewMapper;
    private final IProductCompositeService productService;

    @Autowired
    public ThymeleafController(RecommendationMapper recommendationMapper, ReviewMapper reviewMapper, IProductCompositeService productService) {
        this.recommendationMapper = recommendationMapper;
        this.reviewMapper = reviewMapper;
        this.productService = productService;
    }

    @Override
    public String homePage(Model model) {
        model.addAttribute("message", "Hello from Thymeleaf!");
        return "home";
    }

    @Override
    public String getAllProducts(Model model) {
        Iterable<Product> allProducts = productService.findAll();
        List<ProductAggregateDTO> products = new ArrayList<>();
        allProducts.forEach(p -> products.add(createProductAggregate(p,
                productService.findRecommendationsByProductId(p.getProductId()),
                productService.findReviewsByProductId(p.getProductId()))));
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
            Optional<Product> product = productService.findById(productId);
            Set<Recommendation> recommendations = productService.findRecommendationsByProductId(product.get().getProductId());
            Set<Review> reviews = productService.findReviewsByProductId(product.get().getProductId());
            ProductAggregateDTO productDTO = createProductAggregate(product.get(), recommendations, reviews);
            model.addAttribute("product", productDTO);
            return "product_item";
        }catch (NotFoundException e){
            throw new NotFoundException("No product found with ID: " + productId);
        }
    }

    @Override
    public String createProduct(Model model, @ModelAttribute("product") ProductAggregateDTO request){
        try{
            LOG.debug("createProduct: creates a new composite entity for productId: {}", request.productId());
            Set<Recommendation> recommendations = new HashSet<>();
            Set<Review> reviews = new HashSet<>();
            if (request.recommendations() != null) {
                recommendations = recommendationMapper.recommendationDTOsToRecommendations(request.recommendations());
            }
            if (request.reviews() != null) {
                reviews = reviewMapper.reviewDTOsToReviews(request.reviews());
            }

            Product product = new Product(recommendations, reviews, request.name(), request.weight(), request.version());
            productService.save(product);
            LOG.debug("createProduct: product {} created", request.productId());
        }catch (RuntimeException re) {
            LOG.warn("createProduct failed", re);
            throw re;
        }
        // redirect to products list
        return "redirect:/products";
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

    @Override
    public String createRecommendation(Model model, @PathVariable Long productId, @ModelAttribute("recommendation") RecommendationDTO recommendation){
        try{
            LOG.debug("createRecommendation: creates a new recommendation entity for productId: {}", productId);
            productService.findById(productId);
            Recommendation r = new Recommendation(productId, recommendation.author(), recommendation.rating(), recommendation.content());
            productService.saveRecommendation(r);
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
            productService.deleteRecommendation(productId, recommendationId);
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
            productService.findById(productId);
            Review r = new Review(productId, review.author(), review.subject(), review.content());
            productService.saveReview(r);
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
            productService.deleteReview(productId, reviewId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("Review with ID: " + reviewId + " not found");
        }
        LOG.debug("deleteReview: review deleted for ID: {}", reviewId);
        // redirect to product page
        return ("redirect:/products/" + productId);
    }

    private ProductAggregateDTO createProductAggregate(
            Product p,
            Set<Recommendation> recommendations,
            Set<Review> reviews){

        // 1. Setup product info
        Long productId = p.getProductId();
        String name = p.getName();
        int weight = p.getWeight();
        Integer version = p.getVersion();

        // 2. Copy summary recommendation info, if available
        List<RecommendationDTO> recommendationDTOs = (recommendations == null) ? null :
                recommendations.stream()
                        .map(r -> new RecommendationDTO(r.getRecommendationId(), r.getProductId(), r.getVersion(), r.getAuthor(), r.getRating(), r.getContent()))
                        .toList();

        // 3. Copy summary review info, if available
        List<ReviewDTO> reviewDTOs = (reviews == null) ? null :
                reviews.stream()
                        .map(r -> new ReviewDTO(r.getReviewId(), r.getProductId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .toList();

        return new ProductAggregateDTO(productId, version, name, weight, recommendationDTOs, reviewDTOs);
    }
}
