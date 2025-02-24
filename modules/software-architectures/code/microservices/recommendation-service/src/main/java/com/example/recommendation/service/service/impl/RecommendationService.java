package com.example.recommendation.service.service.impl;

import com.example.recommendation.service.mapper.RecommendationMapper;
import com.example.recommendation.service.persistence.model.Recommendation;
import com.example.recommendation.service.persistence.repository.RecommendationRepository;
import com.example.recommendation.service.service.IRecommendationService;
import com.example.recommendation.service.web.dto.RecommendationDTO;
import com.example.recommendation.service.web.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RecommendationService implements IRecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationService.class);
    private final RecommendationRepository repo;
    private final RecommendationMapper mapper;
    private final WebClient webClient;

    public RecommendationService(RecommendationRepository recommendationRepository, RecommendationMapper recommendationMapper,  WebClient.Builder webClientBuilder) {
        this.repo = recommendationRepository;
        this.mapper = recommendationMapper;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public RecommendationDTO save(RecommendationDTO recommendation) {
        try{
            return mapper.toDTO(repo.save(mapper.toEntity(recommendation)));
        }catch (RuntimeException re){
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    @Override
    public void saveRecommendationOnProductCreation(RecommendationDTO recommendation) {
        repo.save(mapper.toEntity(recommendation));
    }

    @Override
    public void deleteById(Long productId, Long recommendationId) {
        Optional<Recommendation> p = repo.findById(recommendationId);
        if(p.isEmpty())
            throw new NotFoundException();
        try {
            repo.deleteRecommendationById(recommendationId);
            repo.flush();
        }catch (NoSuchElementException e) {
            throw new NotFoundException("No recommendation found with ID: " + recommendationId);
        }
    }

    @Override
    public void deleteRecommendations(Long productId) {
        try{
            repo.deleteAllRecommendationsByProductId(productId);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("No product found with ID: " + productId);
        }
    }

    @Override
    public List<RecommendationDTO> findRecommendationsByProductId(Long productId) {
        try{
            return mapper.toDTOs(repo.findRecommendationsByProductId(productId));
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("No product found with ID: " + productId);
        }
    }


}
