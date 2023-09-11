package com.pivo.weev.backend.jpa.repository.wrapper;

import com.pivo.weev.backend.jpa.model.user.UserJpa;
import com.pivo.weev.backend.jpa.repository.IUserRepository;
import com.pivo.weev.backend.jpa.model.common.ResourceName;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryWrapper extends GenericRepositoryWrapper<Long, UserJpa, IUserRepository> {

  public UserRepositoryWrapper(IUserRepository repository) {
    super(repository, ResourceName.USER);
  }
}
