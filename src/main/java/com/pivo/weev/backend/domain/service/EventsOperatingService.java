package com.pivo.weev.backend.domain.service;

import static com.pivo.weev.backend.common.utils.CollectionUtils.findFirst;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.SUBCATEGORY_NOT_FOUND_ERROR;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.EventJpaMapper;
import com.pivo.weev.backend.domain.mapping.LocationJpaMapper;
import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.domain.model.file.Image;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.CategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.SubcategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventCategoryRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.LocationRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRepositoryWrapper;
import com.pivo.weev.backend.domain.service.files.FilesUploadingService;
import com.pivo.weev.backend.domain.service.files.FilesCompressingService;
import com.pivo.weev.backend.domain.service.validation.EventsValidationService;
import jakarta.transaction.Transactional;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsOperatingService {

    private final LocationRepositoryWrapper locationRepository;
    private final EventRepositoryWrapper eventRepository;
    private final EventCategoryRepositoryWrapper eventCategoryRepository;
    private final UserRepositoryWrapper userRepository;

    private final FilesUploadingService filesUploadingService;
    private final FilesCompressingService filesCompressingService;
    private final EventsValidationService eventsValidationService;
    private final TimeZoneService timeZoneService;

    @Transactional
    public void saveEvent(Event sample) {
        setTimeZones(sample);
        eventsValidationService.validate(sample);
        EventJpa jpaEvent = preparePersistableEvent(sample);
        eventRepository.save(jpaEvent);
    }

    private void setTimeZones(Event sample) {
        Location location = sample.getLocation();
        ZoneId zoneId = timeZoneService.getZoneId(location.getLtd(), location.getLng());
        sample.setStartTimeZoneId(zoneId.getId());
        sample.setEndTimeZoneId(zoneId.getId());
    }

    private EventJpa preparePersistableEvent(Event sample) {
        EventJpa eventJpa = getMapper(EventJpaMapper.class).map(sample);
        eventJpa.setLocation(getLocation(sample));
        eventJpa.setCategory(retrieveCategory(sample));
        eventJpa.setSubcategory(retrieveSubcategory(eventJpa.getCategory(), sample));
        processPhoto(eventJpa, sample);
        UserJpa creator = userRepository.fetch(getUserId());
        eventJpa.setCreator(creator);
        return eventJpa;
    }

    private LocationJpa getLocation(Event sample) {
        Location location = sample.getLocation();
        return locationRepository.findByCoordinates(location.getLng(), location.getLtd())
                                 .orElseGet(() -> locationRepository.save(getMapper(LocationJpaMapper.class).map(location)));
    }

    private CategoryJpa retrieveCategory(Event sample) {
        return eventCategoryRepository.fetchByName(sample.getCategory());
    }

    private SubcategoryJpa retrieveSubcategory(CategoryJpa categoryJpa, Event sample) {
        return findFirst(categoryJpa.getSubcategories(), subcategoryJpa -> equalsIgnoreCase(subcategoryJpa.getName(), sample.getSubcategory()))
                .orElseThrow(() -> new ReasonableException(SUBCATEGORY_NOT_FOUND_ERROR));
    }

    private void processPhoto(EventJpa eventJpa, Event sample) {
        if (!sample.hasPhoto()) {
            return;
        }
        Image compressedPhoto = filesCompressingService.compress(sample.getPhoto());
        CloudResourceJpa cloudResourceJpa = filesUploadingService.upload(compressedPhoto);
        eventJpa.setPhoto(cloudResourceJpa);
    }

}
