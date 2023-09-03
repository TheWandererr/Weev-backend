package com.pivo.weev.backend.dao.repository.wrapper;

import com.pivo.weev.backend.dao.model.UserJpa;
import com.pivo.weev.backend.dao.repository.IUserRepository;
import com.pivo.weev.backend.dao.model.common.ResourceName;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryWrapper extends GenericRepositoryWrapper<Long, UserJpa, IUserRepository> {

  public UserRepositoryWrapper(IUserRepository repository) {
    super(repository, ResourceName.USER);
  }
}
