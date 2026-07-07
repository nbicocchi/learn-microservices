package com.nbicocchi.user.persistence.repository;

import com.nbicocchi.user.persistence.model.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Long> {
    Optional<UserModel> findByUserUUID(String userUUID);
}
