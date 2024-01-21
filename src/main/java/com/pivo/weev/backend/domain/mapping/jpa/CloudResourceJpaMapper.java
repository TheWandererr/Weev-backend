package com.pivo.weev.backend.domain.mapping.jpa;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;

import com.google.cloud.storage.Blob;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import java.net.URL;
import org.mapstruct.Mapper;

@Mapper()
public interface CloudResourceJpaMapper {

    default CloudResourceJpa map(Blob source, URL downloadUrl) {
        CloudResourceJpa destination = new CloudResourceJpa();
        destination.setAuthorId(getUserId());
        destination.setBlobId(source.getBlobId().getName());
        destination.setFormat(source.getContentType());
        destination.setUrl(downloadUrl.toString());
        return destination;
    }
}
