package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.CLOUD_RESOURCE;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.ICloudResourceRepository;
import org.springframework.stereotype.Component;

@Component
public class CloudResourceRepository extends GenericRepository<Long, CloudResourceJpa, ICloudResourceRepository> {

    public CloudResourceRepository(ICloudResourceRepository repository) {
        super(repository, CLOUD_RESOURCE);
    }
}
