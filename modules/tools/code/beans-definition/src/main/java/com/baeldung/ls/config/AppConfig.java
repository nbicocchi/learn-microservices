package com.baeldung.ls.config;

import com.baeldung.ls.service.IProjectService;
import com.baeldung.ls.service.impl.ProjectServiceImpl;
import com.baeldung.other.persistence.repository.IProjectRepository;
import com.baeldung.other.persistence.repository.impl.ProjectRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public IProjectRepository iProjectRepository() {
        return new ProjectRepositoryImpl();
    }

    @Bean
    public IProjectService iProjectService(IProjectRepository projectRepository) {
        return new ProjectServiceImpl(projectRepository);
    }
}
