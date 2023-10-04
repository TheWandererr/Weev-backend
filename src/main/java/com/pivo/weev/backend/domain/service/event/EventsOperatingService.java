package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.common.utils.CollectionUtils.findFirst;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.SUBCATEGORY_NOT_FOUND_ERROR;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.EventJpaMapper;
import com.pivo.weev.backend.domain.mapping.jpa.LocationJpaMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.domain.model.file.Image;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.CategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.SubcategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.CloudResourceRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventCategoryRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.LocationRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRepositoryWrapper;
import com.pivo.weev.backend.domain.service.TimeZoneService;
import com.pivo.weev.backend.domain.service.files.FilesCloudService;
import com.pivo.weev.backend.domain.service.files.FilesCompressingService;
import com.pivo.weev.backend.domain.service.validation.EventsOperationsValidator;
import jakarta.transaction.Transactional;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventsOperatingService {

    private final LocationRepositoryWrapper locationRepository;
    private final EventRepositoryWrapper eventRepository;
    private final EventCategoryRepositoryWrapper eventCategoryRepository;
    private final UserRepositoryWrapper userRepository;
    private final CloudResourceRepositoryWrapper cloudResourceRepository;

    private final FilesCloudService filesCloudService;
    private final FilesCompressingService filesCompressingService;
    private final EventsOperationsValidator eventsOperationsValidator;
    private final TimeZoneService timeZoneService;

    @Transactional
    public void saveEvent(CreatableEvent sample) {
        setTimeZones(sample);
        eventsOperationsValidator.validateCreation(sample);
        EventJpa jpaEvent = preparePersistableEvent(sample);
        eventRepository.save(jpaEvent);
    }

    private void setTimeZones(CreatableEvent sample) {
        MapPoint mapPoint = sample.getLocation()
                                  .getPoint();
        ZoneId zoneId = timeZoneService.getZoneId(mapPoint.getLtd(), mapPoint.getLng());
        sample.setStartTimeZoneId(zoneId.getId());
        sample.setEndTimeZoneId(zoneId.getId());
    }

    private EventJpa preparePersistableEvent(CreatableEvent sample) {
        EventJpa eventJpa = getMapper(EventJpaMapper.class).map(sample);
        eventJpa.setLocation(getLocation(sample));
        eventJpa.setCategory(retrieveCategory(sample));
        eventJpa.setSubcategory(retrieveSubcategory(eventJpa.getCategory(), sample));
        processPhoto(eventJpa, sample);

        UserJpa creator = userRepository.fetch(getUserId());
        eventJpa.setCreator(creator);
        creator.getCreatedEvents().add(eventJpa);

        return eventJpa;
    }

    private LocationJpa getLocation(CreatableEvent sample) {
        Location location = sample.getLocation();
        MapPoint mapPoint = location.getPoint();
        return locationRepository.findByCoordinates(mapPoint.getLng(), mapPoint.getLtd())
                                 .orElseGet(() -> locationRepository.save(getMapper(LocationJpaMapper.class).map(location)));
    }

    private CategoryJpa retrieveCategory(CreatableEvent sample) {
        return eventCategoryRepository.fetchByName(sample.getCategory());
    }

    private SubcategoryJpa retrieveSubcategory(CategoryJpa categoryJpa, CreatableEvent sample) {
        return findFirst(categoryJpa.getSubcategories(), subcategoryJpa -> equalsIgnoreCase(subcategoryJpa.getName(), sample.getSubcategory()))
                .orElseThrow(() -> new ReasonableException(SUBCATEGORY_NOT_FOUND_ERROR));
    }

    private void processPhoto(EventJpa eventJpa, CreatableEvent sample) {
        if (!sample.isUpdatePhoto()) {
            return;
        }
        if (!sample.hasPhoto()) {
            deletePhoto(eventJpa);
        } else if (eventJpa.hasPhoto()) {
            replacePhoto(eventJpa, sample.getPhoto());
        } else {
            createPhoto(eventJpa, sample.getPhoto());
        }
    }

    public void deletePhoto(EventJpa eventJpa) {
        if (!eventJpa.hasPhoto()) {
            return;
        }
        CloudResourceJpa photo = eventJpa.getPhoto();
        cloudResourceRepository.delete(photo);
        filesCloudService.delete(photo.getExternalId());
    }

    private void replacePhoto(EventJpa eventJpa, MultipartFile photo) {
        deletePhoto(eventJpa);
        createPhoto(eventJpa, photo);
    }

    private void createPhoto(EventJpa eventJpa, MultipartFile photo) {
        Image compressedPhoto = filesCompressingService.compress(photo);
        CloudResourceJpa cloudResourceJpa = filesCloudService.upload(compressedPhoto);
        eventJpa.setPhoto(cloudResourceJpa);
    }

    @Transactional
    public void updateEvent(CreatableEvent sample) {
        setTimeZones(sample);
        EventJpa updatableTarget = eventRepository.fetch(sample.getId());
        eventsOperationsValidator.validateUpdate(updatableTarget, sample);

        EventJpa jpaEvent = preparePersistableEvent(sample);
        jpaEvent.setUpdatableTarget(updatableTarget);

        if (updatableTarget.isOnModeration()) {
            replaceExistingEvent(jpaEvent, updatableTarget);
        } else {
            eventRepository.findByUpdatableTargetId(sample.getId())
                           .ifPresentOrElse(existingJpaEvent -> replaceExistingEvent(jpaEvent, existingJpaEvent), () -> {
                               updatableTarget.setStatus(HAS_MODERATION_INSTANCE);
                               eventRepository.save(jpaEvent);
                           });
        }
    }

    private void replaceExistingEvent(EventJpa jpaEvent, EventJpa existingJpaEvent) {
        deletePhoto(existingJpaEvent);
        getMapper(EventJpaMapper.class).map(jpaEvent, existingJpaEvent);
    }
}
