package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.model.meet.Restrictions.Availability.PUBLIC;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CANCELLATION;
import static com.pivo.weev.backend.utils.CollectionUtils.findFirst;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.SUBCATEGORY_NOT_FOUND_ERROR;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.MeetJpaMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.meet.CreatableMeet;
import com.pivo.weev.backend.domain.model.meet.Restrictions.Availability;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.CategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.SubcategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetCategoryRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import com.pivo.weev.backend.domain.service.LocationService;
import com.pivo.weev.backend.domain.service.TimeZoneService;
import com.pivo.weev.backend.domain.service.event.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent;
import com.pivo.weev.backend.domain.service.message.NotificationService;
import com.pivo.weev.backend.domain.service.validation.MeetOperationsValidator;
import java.time.ZoneId;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final MeetTemplatesService meetTemplatesService;


    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApplicationEventFactory applicationEventFactory;

    @Transactional
    public void create(CreatableMeet sample) {
        setTimeZones(sample);
        meetOperationsValidator.validateCreation(sample);
        MeetJpa meet = preparePersistableMeet(sample);
        meetRepository.save(meet);
        if (sample.isSaveAsTemplate()) {
            meetTemplatesService.saveAsTemplate(meet);
        }
    }

    private void setTimeZones(CreatableMeet sample) {
        MapPoint mapPoint = sample.getLocation()
                                  .getPoint();
        ZoneId zoneId = timeZoneService.resolveTimeZone(mapPoint);
        sample.setStartTimeZoneId(zoneId.getId());
        sample.setEndTimeZoneId(zoneId.getId());
    }

    private MeetJpa preparePersistableMeet(CreatableMeet sample) {
        UserJpa creator = usersRepository.fetch(getUserId());
        MeetJpa meetJpa = new MeetJpa(creator);
        getMapper(MeetJpaMapper.class).map(sample, meetJpa);
        meetJpa.setLocation(locationService.resolveLocation(sample));
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

        MeetJpa meet = preparePersistableMeet(sample);
        meet.setUpdatableTarget(updatableTarget);

        if (updatableTarget.isOnModeration() || updatableTarget.isCanceled() || updatableTarget.isDeclined()) {
            replaceExistingMeet(meet, updatableTarget);
        } else {
            meetRepository.findByUpdatableTargetId(sample.getId())
                          .ifPresentOrElse(existingMeet -> replaceExistingMeet(meet, existingMeet), () -> {
                              updatableTarget.setStatus(HAS_MODERATION_INSTANCE);
                              meetRepository.save(meet);
                          });
        }
    }

    private void replaceExistingMeet(MeetJpa meet, MeetJpa existingMeet) {
        meetPhotoService.deletePhoto(existingMeet);
        getMapper(MeetJpaMapper.class).update(meet, existingMeet);
    }

    @Transactional
    public void cancel(Long id) {
        MeetJpa cancellable = meetRepository.fetch(id);
        meetOperationsValidator.validateCancellation(cancellable);

        Set<UserJpa> dissolvedMembers = cancellable.dissolve();
        notifyAll(cancellable, dissolvedMembers, MEET_CANCELLATION);

        meetRepository.deleteByUpdatableTargetId(id);
        cancellable.setStatus(CANCELED);
    }

    private void notifyAll(MeetJpa meet, Set<UserJpa> recipients, String topic) {
        notificationService.notifyAll(meet, recipients, topic);
        PushNotificationEvent event = applicationEventFactory.buildNotificationEvent(meet, recipients, topic);
        applicationEventPublisher.publishEvent(event);
    }

    @Transactional
    public void delete(Long id) {
        MeetJpa deletable = meetRepository.fetch(id);
        meetOperationsValidator.validateDeletion(deletable);
        meetPhotoService.deletePhoto(deletable);
        meetRepository.logicalDelete(deletable);
    }

    @Transactional
    public void join(Long meetId, Long joinerId) {
        MeetJpa meet = meetRepository.fetch(meetId);
        meetOperationsValidator.validateJoin(meet, joinerId, PUBLIC);
        UserJpa joiner = usersRepository.fetch(joinerId);
        meet.addMember(joiner);
    }

    public void joinViaRequest(MeetJpa meet, UserJpa joiner) {
        RestrictionsJpa restrictions = meet.getRestrictions();
        meetOperationsValidator.validateJoin(meet, joiner.getId(), Availability.valueOf(restrictions.getAvailability()));
        meet.addMember(joiner);
    }
}
