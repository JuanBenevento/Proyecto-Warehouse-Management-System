package com.juanbenevento.wms.application.ports.out;

import com.juanbenevento.wms.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}