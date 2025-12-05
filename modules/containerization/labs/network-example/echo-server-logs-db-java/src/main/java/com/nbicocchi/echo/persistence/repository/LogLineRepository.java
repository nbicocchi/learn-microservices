package com.nbicocchi.echo.persistence.repository;

import com.nbicocchi.echo.persistence.model.LogLine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogLineRepository extends CrudRepository<LogLine, Long> {

}
