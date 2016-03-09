package org.seedstack.hub;

import org.seedstack.hub.domain.model.user.User;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.model.user.UserRepository;
import org.seedstack.seed.LifecycleListener;

import javax.inject.Inject;

public class DataLifecycleListener implements LifecycleListener {
    @Inject
    UserRepository userRepository;

    @Override
    public void start() {
        userRepository.persist(new User(new UserId("adrienlauer"), "adrien.lauer@mpsa.com"));
        userRepository.persist(new User(new UserId("pith"), "pierre.thirouin@ext.mpsa.com"));
        userRepository.persist(new User(new UserId("kavi87"), "kavi.ramyead@ext.mpsa.com"));
    }

    @Override
    public void stop() {
        userRepository.delete(new UserId("adrienlauer"));
        userRepository.delete(new UserId("pith"));
        userRepository.delete(new UserId("kavi87"));
    }
}
