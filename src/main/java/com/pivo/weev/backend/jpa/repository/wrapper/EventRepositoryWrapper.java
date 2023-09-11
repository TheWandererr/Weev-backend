package com.pivo.weev.backend.jpa.repository.wrapper;

import static com.pivo.weev.backend.jpa.model.common.ResourceName.EVENT;

import com.pivo.weev.backend.jpa.model.event.EventJpa;
import com.pivo.weev.backend.jpa.repository.IEventRepository;
import org.springframework.stereotype.Component;

@Component
public class EventRepositoryWrapper extends GenericRepositoryWrapper<Long, EventJpa, IEventRepository> {

  protected EventRepositoryWrapper(IEventRepository repository) {
    super(repository, EVENT);
  }
}
