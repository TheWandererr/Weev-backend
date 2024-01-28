package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.domain.model.event.Restrictions.Availability.PUBLIC;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_CANCELLATION;
import static com.pivo.weev.backend.utils.CollectionUtils.findFirst;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.SUBCATEGORY_NOT_FOUND_ERROR;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.EventJpaMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.event.Restrictions.Availability;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.CategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.RestrictionsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.SubcategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventCategoryRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import com.pivo.weev.backend.domain.service.LocationService;
import com.pivo.weev.backend.domain.service.NotificationService;
import com.pivo.weev.backend.domain.service.TimeZoneService;
import com.pivo.weev.backend.domain.service.validation.EventsOperationsValidator;
import java.time.ZoneId;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventsOperationsService {

    private final EventsRepositoryWrapper eventsRepository;
    private final EventCategoryRepositoryWrapper eventCategoryRepository;
    private final UsersRepositoryWrapper usersRepository;

    private final EventsOperationsValidator eventsOperationsValidator;
    private final LocationService locationService;
    private final TimeZoneService timeZoneService;
    private final EventsPhotoService eventsPhotoService;
    private final NotificationService notificationService;

    @Transactional
    public void create(CreatableEvent sample) {
        setTimeZones(sample);
        eventsOperationsValidator.validateCreation(sample);
        EventJpa jpaEvent = preparePersistableEvent(sample);
        eventsRepository.save(jpaEvent);
    }

    private void setTimeZones(CreatableEvent sample) {
        MapPoint mapPoint = sample.getLocation()
                                  .getPoint();
        ZoneId zoneId = timeZoneService.resolveTimeZone(mapPoint);
        sample.setStartTimeZoneId(zoneId.getId());
        sample.setEndTimeZoneId(zoneId.getId());
    }

    private EventJpa preparePersistableEvent(CreatableEvent sample) {
        UserJpa creator = usersRepository.fetch(getUserId());
        EventJpa eventJpa = new EventJpa(creator);
        getMapper(EventJpaMapper.class).map(sample, eventJpa);
        LocationJpa location = locationService.resolveLocation(sample);
        eventJpa.setLocation(location);
        eventJpa.setCategory(resolveCategory(sample));
        eventJpa.setSubcategory(resolveSubcategory(eventJpa.getCategory(), sample));
        eventsPhotoService.updatePhoto(sample, eventJpa);
        return eventJpa;
    }

    private CategoryJpa resolveCategory(CreatableEvent sample) {
        return eventCategoryRepository.fetchByName(sample.getCategory());
    }

    private SubcategoryJpa resolveSubcategory(CategoryJpa categoryJpa, CreatableEvent sample) {
        return findFirst(categoryJpa.getSubcategories(), subcategoryJpa -> equalsIgnoreCase(subcategoryJpa.getName(), sample.getSubcategory()))
                .orElseThrow(() -> new FlowInterruptedException(SUBCATEGORY_NOT_FOUND_ERROR));
    }

    @Transactional
    public void update(CreatableEvent sample) {
        setTimeZones(sample);
        EventJpa updatableTarget = eventsRepository.fetch(sample.getId());
        eventsOperationsValidator.validateUpdate(updatableTarget, sample);

        EventJpa jpaEvent = preparePersistableEvent(sample);
        jpaEvent.setUpdatableTarget(updatableTarget);

        if (updatableTarget.isOnModeration() || updatableTarget.isCanceled() || updatableTarget.isDeclined()) {
            replaceExistingEvent(jpaEvent, updatableTarget);
        } else {
            eventsRepository.findByUpdatableTargetId(sample.getId())
                            .ifPresentOrElse(existingJpaEvent -> replaceExistingEvent(jpaEvent, existingJpaEvent), () -> {
                                updatableTarget.setStatus(HAS_MODERATION_INSTANCE);
                                eventsRepository.save(jpaEvent);
                            });
        }
    }

    private void replaceExistingEvent(EventJpa jpaEvent, EventJpa existingJpaEvent) {
        eventsPhotoService.deletePhoto(existingJpaEvent);
        getMapper(EventJpaMapper.class).remap(jpaEvent, existingJpaEvent);
    }

    @Transactional
    public void cancel(Long id) {
        EventJpa cancellable = eventsRepository.fetch(id);
        eventsOperationsValidator.validateCancellation(cancellable);

        Set<UserJpa> dissolvedMembers = cancellable.dissolve();
        notificationService.notifyAll(cancellable, dissolvedMembers, EVENT_CANCELLATION);

        eventsRepository.deleteByUpdatableTargetId(id);
        cancellable.setStatus(CANCELED);
    }

    @Transactional
    public void delete(Long id) {
        EventJpa deletable = eventsRepository.fetch(id);
        eventsOperationsValidator.validateDeletion(deletable);
        eventsPhotoService.deletePhoto(deletable);
        eventsRepository.logicalDelete(deletable);
    }

    @Transactional
    public void join(Long eventId, Long joinerId) {
        EventJpa event = eventsRepository.fetch(eventId);
        eventsOperationsValidator.validateJoin(event, joinerId, PUBLIC);
        UserJpa joiner = usersRepository.fetch(joinerId);
        event.addMember(joiner);
    }

    public void joinViaRequest(EventJpa event, UserJpa joiner) {
        RestrictionsJpa restrictions = event.getRestrictions();
        eventsOperationsValidator.validateJoin(event, joiner.getId(), Availability.valueOf(restrictions.getAvailability()));
        event.addMember(joiner);
    }
}
