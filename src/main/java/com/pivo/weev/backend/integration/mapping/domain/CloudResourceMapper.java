package com.pivo.weev.backend.integration.mapping.domain;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;

import com.google.cloud.storage.Blob;
import com.pivo.weev.backend.domain.model.common.CloudResource;
import java.net.URL;
import org.mapstruct.Mapper;

@Mapper
public interface CloudResourceMapper {

    default CloudResource map(Blob source, URL downloadUrl) {
        CloudResource destination = new CloudResource();
        destination.setAuthorId(getUserId());
        destination.setBlobId(source.getBlobId().getName());
        destination.setFormat(source.getContentType());
        destination.setUrl(downloadUrl.toString());
        return destination;
    }
}
