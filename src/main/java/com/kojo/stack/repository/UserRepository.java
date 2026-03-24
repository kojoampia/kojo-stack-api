package com.kojo.stack.repository;

import com.kojo.stack.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findOneByActivationKey(String activationKey);
    Optional<User> findOneByResetKey(String resetKey);
    Optional<User> findOneByEmailIgnoreCase(String email);
    Optional<User> findOneByLogin(String login);

    List<User> findAllByIdNotNull(Pageable pageable);
    List<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);   

}
