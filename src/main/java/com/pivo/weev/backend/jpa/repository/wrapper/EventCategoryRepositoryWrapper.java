package com.pivo.weev.backend.jpa.repository.wrapper;

import static com.pivo.weev.backend.jpa.model.common.ResourceName.EVENT_CATEGORY;

import com.pivo.weev.backend.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.jpa.model.event.CategoryJpa;
import com.pivo.weev.backend.jpa.repository.IEventCategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class EventCategoryRepositoryWrapper extends GenericRepositoryWrapper<Long, CategoryJpa, IEventCategoryRepository> {

  protected EventCategoryRepositoryWrapper(IEventCategoryRepository repository) {
    super(repository, EVENT_CATEGORY);
  }

  public CategoryJpa fetchByName(String name) {
    return repository.findByName(name)
                     .orElseThrow(() -> new ResourceNotFoundException(notFound()));
  }
}
