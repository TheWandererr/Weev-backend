package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.CLOUD_RESOURCE;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.ICloudResourceRepository;
import org.springframework.stereotype.Component;

@Component
public class CloudResourceRepositoryWrapper extends GenericRepositoryWrapper<Long, CloudResourceJpa, ICloudResourceRepository> {

    public CloudResourceRepositoryWrapper(ICloudResourceRepository repository) {
        super(repository, CLOUD_RESOURCE);
    }
}
