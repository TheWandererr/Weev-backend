package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.CategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IEventCategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class EventCategoryRepositoryWrapper extends GenericRepositoryWrapper<Long, CategoryJpa, IEventCategoryRepository> {

    protected EventCategoryRepositoryWrapper(IEventCategoryRepository repository) {
        super(repository, ResourceName.EVENT_CATEGORY);
    }

    public CategoryJpa fetchByName(String name) {
        return repository.findByName(name)
                         .orElseThrow(() -> new ResourceNotFoundException(notFound()));
    }
}
