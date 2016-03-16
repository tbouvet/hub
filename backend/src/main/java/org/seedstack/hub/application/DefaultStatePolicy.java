package org.seedstack.hub.application;

import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.seed.security.AuthenticationException;

import javax.inject.Inject;

public class DefaultStatePolicy implements StatePolicy {

    @Inject
    private SecurityService securityService;

    @Override
    public boolean canPublish(Component component) {
        return securityService.isUserAdmin();
    }

    @Override
    public boolean canArchive(Component component) {
        UserId userId = securityService.getAuthenticatedUser()
            .orElseThrow(AuthenticationException::new).getEntityId();
        return securityService.isUserAdmin() || component.getOwner().equals(userId);
    }
}
