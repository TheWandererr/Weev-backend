package com.pivo.weev.backend.domain.service;

import static com.pivo.weev.backend.common.utils.CollectionUtils.findFirst;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.SUBCATEGORY_NOT_FOUND_ERROR;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.CloudResourceJpaMapper;
import com.pivo.weev.backend.domain.mapping.EventJpaMapper;
import com.pivo.weev.backend.domain.mapping.LocationJpaMapper;
import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.domain.service.validation.EventsValidationService;
import com.pivo.weev.backend.integration.client.cloudinary.model.Image;
import com.pivo.weev.backend.integration.service.cloudinary.CloudinaryService;
import com.pivo.weev.backend.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.jpa.model.event.CategoryJpa;
import com.pivo.weev.backend.jpa.model.event.EventJpa;
import com.pivo.weev.backend.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.jpa.model.event.SubcategoryJpa;
import com.pivo.weev.backend.jpa.model.user.UserJpa;
import com.pivo.weev.backend.jpa.repository.wrapper.EventCategoryRepositoryWrapper;
import com.pivo.weev.backend.jpa.repository.wrapper.EventRepositoryWrapper;
import com.pivo.weev.backend.jpa.repository.wrapper.LocationRepositoryWrapper;
import com.pivo.weev.backend.jpa.repository.wrapper.UserRepositoryWrapper;
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

    private final CloudinaryService cloudinaryService;
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
        eventJpa.setPhoto(uploadPhoto(sample));
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

    private CloudResourceJpa uploadPhoto(Event sample) {
        if (!sample.hasPhoto()) {
            return null;
        }
        Image image = cloudinaryService.upload(sample.getPhoto());
        return getMapper(CloudResourceJpaMapper.class).map(image);
    }

}
