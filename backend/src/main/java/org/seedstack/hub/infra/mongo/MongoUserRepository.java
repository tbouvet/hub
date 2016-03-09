package org.seedstack.hub.infra.mongo;

import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.mongodb.morphia.BaseMorphiaRepository;

import java.util.Optional;

public class MongoUserRepository extends BaseMorphiaRepository<User, UserId> implements UserRepository {
    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(getDatastore().createQuery(User.class).field("email").equal(email).get());
    }
}
