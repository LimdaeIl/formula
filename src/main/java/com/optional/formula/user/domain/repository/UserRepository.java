package com.optional.formula.user.domain.repository;

import com.optional.formula.user.domain.entity.User;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long userId);

    void delete(User user);
}
