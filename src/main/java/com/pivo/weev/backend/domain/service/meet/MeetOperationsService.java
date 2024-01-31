package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.model.meet.Restrictions.Availability.PUBLIC;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.MEET_CANCELLATION;
import static com.pivo.weev.backend.utils.CollectionUtils.findFirst;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.SUBCATEGORY_NOT_FOUND_ERROR;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.MeetJpaMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.meet.CreatableMeet;
import com.pivo.weev.backend.domain.model.meet.Restrictions.Availability;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.CategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.SubcategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetCategoryRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import com.pivo.weev.backend.domain.service.LocationService;
import com.pivo.weev.backend.domain.service.NotificationService;
import com.pivo.weev.backend.domain.service.TimeZoneService;
import com.pivo.weev.backend.domain.service.validation.MeetOperationsValidator;
import java.time.ZoneId;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetOperationsService {

    private final MeetRepositoryWrapper meetRepository;
    private final MeetCategoryRepositoryWrapper meetCategoryRepository;
    private final UsersRepositoryWrapper usersRepository;

    private final MeetOperationsValidator meetOperationsValidator;
    private final LocationService locationService;
    private final TimeZoneService timeZoneService;
    private final MeetPhotoService meetPhotoService;
    private final NotificationService notificationService;

    @Transactional
    public void create(CreatableMeet sample) {
        setTimeZones(sample);
        meetOperationsValidator.validateCreation(sample);
        MeetJpa meet = preparePersistableEvent(sample);
        meetRepository.save(meet);
    }

    private void setTimeZones(CreatableMeet sample) {
        MapPoint mapPoint = sample.getLocation()
                                  .getPoint();
        ZoneId zoneId = timeZoneService.resolveTimeZone(mapPoint);
        sample.setStartTimeZoneId(zoneId.getId());
        sample.setEndTimeZoneId(zoneId.getId());
    }

    private MeetJpa preparePersistableEvent(CreatableMeet sample) {
        UserJpa creator = usersRepository.fetch(getUserId());
        MeetJpa meetJpa = new MeetJpa(creator);
        getMapper(MeetJpaMapper.class).map(sample, meetJpa);
        LocationJpa location = locationService.resolveLocation(sample);
        meetJpa.setLocation(location);
        meetJpa.setCategory(resolveCategory(sample));
        meetJpa.setSubcategory(resolveSubcategory(meetJpa.getCategory(), sample));
        meetPhotoService.updatePhoto(sample, meetJpa);
        return meetJpa;
    }

    private CategoryJpa resolveCategory(CreatableMeet sample) {
        return meetCategoryRepository.fetchByName(sample.getCategory());
    }

    private SubcategoryJpa resolveSubcategory(CategoryJpa categoryJpa, CreatableMeet sample) {
        return findFirst(categoryJpa.getSubcategories(), subcategoryJpa -> equalsIgnoreCase(subcategoryJpa.getName(), sample.getSubcategory()))
                .orElseThrow(() -> new FlowInterruptedException(SUBCATEGORY_NOT_FOUND_ERROR));
    }

    @Transactional
    public void update(CreatableMeet sample) {
        setTimeZones(sample);
        MeetJpa updatableTarget = meetRepository.fetch(sample.getId());
        meetOperationsValidator.validateUpdate(updatableTarget, sample);

        MeetJpa jpaEvent = preparePersistableEvent(sample);
        jpaEvent.setUpdatableTarget(updatableTarget);

        if (updatableTarget.isOnModeration() || updatableTarget.isCanceled() || updatableTarget.isDeclined()) {
            replaceExistingEvent(jpaEvent, updatableTarget);
        } else {
            meetRepository.findByUpdatableTargetId(sample.getId())
                          .ifPresentOrElse(existingJpaEvent -> replaceExistingEvent(jpaEvent, existingJpaEvent), () -> {
                              updatableTarget.setStatus(HAS_MODERATION_INSTANCE);
                              meetRepository.save(jpaEvent);
                          });
        }
    }

    private void replaceExistingEvent(MeetJpa jpaEvent, MeetJpa existingJpaEvent) {
        meetPhotoService.deletePhoto(existingJpaEvent);
        getMapper(MeetJpaMapper.class).remap(jpaEvent, existingJpaEvent);
    }

    @Transactional
    public void cancel(Long id) {
        MeetJpa cancellable = meetRepository.fetch(id);
        meetOperationsValidator.validateCancellation(cancellable);

        Set<UserJpa> dissolvedMembers = cancellable.dissolve();
        notificationService.notifyAll(cancellable, dissolvedMembers, MEET_CANCELLATION);

        meetRepository.deleteByUpdatableTargetId(id);
        cancellable.setStatus(CANCELED);
    }

    @Transactional
    public void delete(Long id) {
        MeetJpa deletable = meetRepository.fetch(id);
        meetOperationsValidator.validateDeletion(deletable);
        meetPhotoService.deletePhoto(deletable);
        meetRepository.logicalDelete(deletable);
    }

    @Transactional
    public void join(Long eventId, Long joinerId) {
        MeetJpa event = meetRepository.fetch(eventId);
        meetOperationsValidator.validateJoin(event, joinerId, PUBLIC);
        UserJpa joiner = usersRepository.fetch(joinerId);
        event.addMember(joiner);
    }

    public void joinViaRequest(MeetJpa event, UserJpa joiner) {
        RestrictionsJpa restrictions = event.getRestrictions();
        meetOperationsValidator.validateJoin(event, joiner.getId(), Availability.valueOf(restrictions.getAvailability()));
        event.addMember(joiner);
    }
}
