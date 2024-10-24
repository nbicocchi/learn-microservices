package com.nbicocchi.monolith.product.domain;

import com.nbicocchi.monolith.util.exceptions.InvalidInputException;
import com.nbicocchi.monolith.product.shared.IProductService;
import com.nbicocchi.monolith.product.shared.ProductDTO;
import com.nbicocchi.monolith.util.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
class ProductService implements IProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repo;
    private final ProductMapper mapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.repo = productRepository;
        this.mapper = productMapper;
    }

    public ProductDTO findById(Long id){
        if (id < 1) {
            throw new InvalidInputException("Invalid productId: " + id);
        }

        Product entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("No product found with ID: " + id));
            return mapper.toDTO(entity);
    }


    public Collection<ProductDTO> findAll(){
        List<ProductDTO> products = new ArrayList<>();
        repo.findAll().forEach(p -> products.add(mapper.toDTO(p)));
        return products;
    }


    public ProductDTO save(ProductDTO product) {
        try {
            return mapper.toDTO(repo.save(mapper.toEntity(product)));
        }catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }


    public void deleteById(Long id) {
        repo.findById(id).ifPresent(repo::delete);
    }
}
