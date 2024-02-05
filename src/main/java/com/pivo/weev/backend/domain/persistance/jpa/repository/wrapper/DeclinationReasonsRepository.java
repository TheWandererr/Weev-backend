package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.DECLINATION_REASON;

import com.pivo.weev.backend.domain.persistance.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.DeclinationReasonJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IDeclinationReasonsRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DeclinationReasonsRepository extends GenericRepository<Long, DeclinationReasonJpa, IDeclinationReasonsRepository> {

    protected DeclinationReasonsRepository(IDeclinationReasonsRepository repository) {
        super(repository, DECLINATION_REASON);
    }

    public List<DeclinationReasonJpa> getAll() {
        return repository.findAll();
    }

    public DeclinationReasonJpa fetchByTitle(String title) {
        return repository.findByTitle(title)
                         .orElseThrow(() -> new ResourceNotFoundException(notFound()));
    }
}
