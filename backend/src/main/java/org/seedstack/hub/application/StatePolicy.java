package org.seedstack.hub.application;

import org.seedstack.business.domain.DomainPolicy;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.ComponentId;

@DomainPolicy
public interface StatePolicy {

    boolean canPublish(Component component);
    boolean canArchive(Component component);
}
