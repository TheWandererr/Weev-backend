package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.DECLINATION_REASON;

import com.pivo.weev.backend.domain.persistance.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IDeclinationReasonsRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DeclinationReasonsRepositoryWrapper extends GenericRepositoryWrapper<Long, DeclinationReason, IDeclinationReasonsRepository> {

    protected DeclinationReasonsRepositoryWrapper(IDeclinationReasonsRepository repository) {
        super(repository, DECLINATION_REASON);
    }

    public List<DeclinationReason> getAll() {
        return repository.findAll();
    }

    public DeclinationReason fetchByTitle(String title) {
        return repository.findByTitle(title)
                         .orElseThrow(() -> new ResourceNotFoundException(notFound()));
    }
}
