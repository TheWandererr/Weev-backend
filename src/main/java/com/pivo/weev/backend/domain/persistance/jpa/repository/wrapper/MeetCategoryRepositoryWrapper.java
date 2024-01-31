package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.CategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IMeetCategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class MeetCategoryRepositoryWrapper extends GenericRepositoryWrapper<Long, CategoryJpa, IMeetCategoryRepository> {

    protected MeetCategoryRepositoryWrapper(IMeetCategoryRepository repository) {
        super(repository, ResourceName.MEET_CATEGORY);
    }

    public CategoryJpa fetchByName(String name) {
        return repository.findByName(name)
                         .orElseThrow(() -> new ResourceNotFoundException(notFound()));
    }
}
