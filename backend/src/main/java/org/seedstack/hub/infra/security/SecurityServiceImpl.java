package org.seedstack.hub.infra.security;

import org.seedstack.hub.application.SecurityService;
import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.security.SecuritySupport;
import org.seedstack.seed.security.principals.Principals;
import org.seedstack.seed.security.principals.SimplePrincipalProvider;

import javax.inject.Inject;
import java.util.Optional;

public class SecurityServiceImpl implements SecurityService {
    @Inject
    UserRepository userRepository;
    @Inject
    SecuritySupport securitySupport;

    @Override
    public Optional<User> getAuthenticatedUser() {
        SimplePrincipalProvider simplePrincipalByName = securitySupport.getSimplePrincipalByName(Principals.IDENTITY);
        if (simplePrincipalByName != null) {
            return Optional.ofNullable(userRepository.load(new UserId(simplePrincipalByName.getValue())));
        } else {
            return Optional.empty();
        }
    }
}
