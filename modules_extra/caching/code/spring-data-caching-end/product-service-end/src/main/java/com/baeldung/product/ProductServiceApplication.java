package com.baeldung.product;

import com.baeldung.product.persistence.ProductEntity;
import com.baeldung.product.persistence.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ProductServiceApplication implements CommandLineRunner {
	private static final Logger LOG = LoggerFactory.getLogger(ProductServiceApplication.class);
	private final MongoOperations mongoTemplate;
	private final ProductRepository productRepository;

	public ProductServiceApplication(MongoOperations mongoTemplate, ProductRepository productRepository) {
		this.mongoTemplate = mongoTemplate;
		this.productRepository = productRepository;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(ProductServiceApplication.class, args);
		String mongoDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
		String mongoDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
        LOG.info("Connected to MongoDb: {}:{}", mongoDbHost, mongoDbPort);
	}

	@EventListener(ContextRefreshedEvent.class)
	public void initIndicesAfterStartup() {
		MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
		IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

		IndexOperations indexOps = mongoTemplate.indexOps(ProductEntity.class);
		resolver.resolveIndexFor(ProductEntity.class).forEach(indexOps::ensureIndex);
	}

	@Override
	public void run(String... args) {
		productRepository.deleteAll().block();
		productRepository.save(new ProductEntity(1, "p-id1", 1)).block();
		productRepository.save(new ProductEntity(2, "p-id2", 2)).block();
		productRepository.save(new ProductEntity(3, "p-id3", 3)).block();
		LOG.info("Logging all data...");
		Iterable<ProductEntity> products = productRepository.findAll().collectList().block();
		for (ProductEntity productEntity : products) {
			LOG.info(productEntity.toString());
		}

	}
}
