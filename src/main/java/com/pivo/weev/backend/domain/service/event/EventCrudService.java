package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.common.utils.CollectionUtils.findFirst;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.SUBCATEGORY_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_CANCELLATION;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.EventJpaMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.CategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.SubcategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventCategoryRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRepositoryWrapper;
import com.pivo.weev.backend.domain.service.LocationService;
import com.pivo.weev.backend.domain.service.NotificationService;
import com.pivo.weev.backend.domain.service.validation.EventCrudValidator;
import jakarta.transaction.Transactional;
import java.time.ZoneId;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventCrudService {

    private final EventRepositoryWrapper eventRepository;
    private final EventCategoryRepositoryWrapper eventCategoryRepository;
    private final UserRepositoryWrapper userRepository;

    private final EventCrudValidator eventCrudValidator;
    private final LocationService locationService;
    private final EventImageService eventImageService;
    private final NotificationService notificationService;

    @Transactional
    public void save(CreatableEvent sample) {
        setTimeZones(sample);
        eventCrudValidator.validateCreation(sample);
        EventJpa jpaEvent = preparePersistableEvent(sample);
        eventRepository.save(jpaEvent);
    }

    private void setTimeZones(CreatableEvent sample) {
        MapPoint mapPoint = sample.getLocation()
                                  .getPoint();
        ZoneId zoneId = locationService.resolveTimeZone(mapPoint);
        sample.setStartTimeZoneId(zoneId.getId());
        sample.setEndTimeZoneId(zoneId.getId());
    }

    private EventJpa preparePersistableEvent(CreatableEvent sample) {
        UserJpa creator = userRepository.fetch(getUserId());
        EventJpa eventJpa = new EventJpa(creator);
        getMapper(EventJpaMapper.class).map(sample, eventJpa);
        LocationJpa location = locationService.resolveLocation(sample);
        eventJpa.setLocation(location);
        eventJpa.setCategory(retrieveCategory(sample));
        eventJpa.setSubcategory(retrieveSubcategory(eventJpa.getCategory(), sample));
        eventImageService.updatePhoto(sample, eventJpa);
        return eventJpa;
    }

    private CategoryJpa retrieveCategory(CreatableEvent sample) {
        return eventCategoryRepository.fetchByName(sample.getCategory());
    }

    private SubcategoryJpa retrieveSubcategory(CategoryJpa categoryJpa, CreatableEvent sample) {
        return findFirst(categoryJpa.getSubcategories(), subcategoryJpa -> equalsIgnoreCase(subcategoryJpa.getName(), sample.getSubcategory()))
                .orElseThrow(() -> new ReasonableException(SUBCATEGORY_NOT_FOUND_ERROR));
    }

    @Transactional
    public void updateEvent(CreatableEvent sample) {
        setTimeZones(sample);
        EventJpa updatableTarget = eventRepository.fetch(sample.getId());
        eventCrudValidator.validateUpdate(updatableTarget, sample);

        EventJpa jpaEvent = preparePersistableEvent(sample);
        jpaEvent.setUpdatableTarget(updatableTarget);

        if (updatableTarget.isOnModeration() || updatableTarget.isCanceled() || updatableTarget.isDeclined()) {
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
        eventImageService.deletePhoto(existingJpaEvent);
        getMapper(EventJpaMapper.class).remap(jpaEvent, existingJpaEvent);
    }

    @Transactional
    public void cancel(Long id) {
        EventJpa cancellable = eventRepository.fetch(id);
        eventCrudValidator.validateCancellation(cancellable);

        Set<UserJpa> dissolvedMembers = cancellable.dissolve();
        notificationService.notifyAll(dissolvedMembers, cancellable, EVENT_CANCELLATION);

        eventRepository.deleteByUpdatableTargetId(id);
        cancellable.setStatus(CANCELED);
    }

    @Transactional
    public void delete(Long id) {
        EventJpa deletable = eventRepository.fetch(id);
        eventCrudValidator.validateDeletion(deletable);
        eventImageService.deletePhoto(deletable);
        eventRepository.logicalDelete(deletable);
    }
}
