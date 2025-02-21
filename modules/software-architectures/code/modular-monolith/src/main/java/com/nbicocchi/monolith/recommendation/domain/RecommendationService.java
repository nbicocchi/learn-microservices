package com.nbicocchi.monolith.recommendation.domain;

import com.nbicocchi.monolith.recommendation.shared.IRecommendationService;
import com.nbicocchi.monolith.recommendation.shared.RecommendationDTO;
import com.nbicocchi.monolith.util.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
class RecommendationService implements IRecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationService.class);
    private final RecommendationRepository repo;
    private final RecommendationMapper mapper;

    public RecommendationService(RecommendationRepository recommendationRepository, RecommendationMapper recommendationMapper) {
        this.repo = recommendationRepository;
        this.mapper = recommendationMapper;
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
    public void deleteById(Long recommendationId) {
        try {
            repo.deleteById(recommendationId);
        }catch (NoSuchElementException e) {
            throw new NotFoundException("No recommendation found with ID: " + recommendationId);
        }

    }

    @Override
    public void deleteRecommendations(Long productId) {
        try{
            repo.deleteAll(repo.findRecommendationsByProductId(productId));
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
