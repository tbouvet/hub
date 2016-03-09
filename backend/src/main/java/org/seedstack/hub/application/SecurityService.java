package org.seedstack.hub.application;

import org.seedstack.business.Service;
import org.seedstack.hub.domain.model.user.User;

import java.util.Optional;

@Service
public interface SecurityService {
    Optional<User> getAuthenticatedUser();
}
