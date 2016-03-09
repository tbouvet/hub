package org.seedstack.hub.domain.model.user;

import org.seedstack.business.domain.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, UserId> {
    Optional<User> findByEmail(String email);
}
