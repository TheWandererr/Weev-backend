package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.persistance.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.Entity;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IGenericRepository;
import com.pivo.weev.backend.domain.persistance.utils.Constants.Errors;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public abstract class GenericRepositoryWrapper<PK extends Serializable, E extends Entity, R extends IGenericRepository<PK, E>> {

    protected final R repository;
    protected final ResourceName resourceName;

    protected GenericRepositoryWrapper(R repository, ResourceName resource) {
        this.repository = repository;
        this.resourceName = resource;
    }

    public E save(E resource) {
        return repository.save(resource);
    }

    public List<E> saveAll(Iterable<E> resources) {
        return repository.saveAll(resources);
    }

    public E fetch(PK id) {
        return find(id).orElseThrow(() -> new ResourceNotFoundException(notFound()));
    }

    public E fetch(Specification<E> specification) {
        return find(specification).orElseThrow(() -> new ResourceNotFoundException(notFound()));
    }

    public void forceDelete(E resource) {
        if (nonNull(resource)) {
            repository.delete(resource);
        }
    }

    public void logicalDelete(E resource) {
        if (nonNull(resource)) {
            resource.logicalDeleted();
        }
    }

    public void forceDeleteById(PK id) {
        repository.deleteById(id);
    }

    public Optional<E> find(PK id) {
        return repository.findById(id);
    }

    public Optional<E> find(Specification<E> specification) {
        return repository.findOne(specification);
    }

    public Page<E> findAll(Specification<E> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    public List<E> findAll(Specification<E> specification) {
        return repository.findAll(specification);
    }

    public List<E> findAll(Iterable<PK> ids) {
        return repository.findAllById(ids);
    }

    public void checkNotExists(Example<E> example, Function<String, ? extends RuntimeException> throwable) {
        final boolean exists = repository.exists(example);
        if (exists) {
            throwable.apply(alreadyExists());
        }
    }

    public boolean existsById(PK id) {
        return repository.existsById(id);
    }

    public boolean exists(Specification<E> specification) {
        return count(specification) > 0;
    }

    public long count(Specification<E> specification) {
        return repository.count(specification);
    }

    public String notFound() {
        return String.format(Errors.REQUESTED_RESOURCE_NOT_FOUND, resourceName.name().toLowerCase());
    }

    public String alreadyExists() {
        return String.format(Errors.REQUESTED_RESOURCE_ALREADY_EXISTS, resourceName.name().toLowerCase());
    }
}
