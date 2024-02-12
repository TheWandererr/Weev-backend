package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.model.meet.Restrictions.Availability.PUBLIC;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CANCELLATION;
import static com.pivo.weev.backend.utils.CollectionUtils.findFirst;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToSet;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.SUBCATEGORY_NOT_FOUND_ERROR;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.MeetJpaMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.meet.CreatableMeet;
import com.pivo.weev.backend.domain.model.meet.Restrictions.Availability;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.CategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.SubcategoryJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetCategoryRepository;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepository;
import com.pivo.weev.backend.domain.service.LocationService;
import com.pivo.weev.backend.domain.service.TimeZoneService;
import com.pivo.weev.backend.domain.service.event.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent;
import com.pivo.weev.backend.domain.service.message.NotificationService;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import com.pivo.weev.backend.domain.service.validation.MeetOperationsValidator;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetOperationsService {

    private final MeetRepository meetRepository;
    private final MeetCategoryRepository meetCategoryRepository;

    private final MeetOperationsValidator meetOperationsValidator;
    private final LocationService locationService;
    private final TimeZoneService timeZoneService;
    private final MeetPhotoService meetPhotoService;
    private final NotificationService notificationService;
    private final MeetTemplatesService meetTemplatesService;
    private final UserResourceService userResourceService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApplicationEventFactory applicationEventFactory;

    @Transactional
    public void create(CreatableMeet sample) {
        setTimeZones(sample);
        meetOperationsValidator.validateCreation(sample);
        sample.setUpdatePhoto(true);
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
        UserJpa creator = userResourceService.fetchUserJpa(getUserId());
        CategoryJpa category = resolveCategory(sample);
        SubcategoryJpa subcategory = resolveSubcategory(category, sample);
        LocationJpa location = locationService.resolveLocation(sample);
        MeetJpa meetJpa = new MeetJpa(creator, category, subcategory, location);
        getMapper(MeetJpaMapper.class).map(sample, meetJpa);
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
        cancel(cancellable, false);
    }

    @Transactional
    public void cancel(MeetJpa cancellable, boolean forced) {
        meetOperationsValidator.validateCancellation(cancellable, forced);

        Set<UserJpa> dissolvedMembers = cancellable.dissolve();
        notifyAll(cancellable, dissolvedMembers, MEET_CANCELLATION);

        cancellable.getRequests().clear();
        meetRepository.deleteByUpdatableTargetId(cancellable.getId());
        cancellable.setStatus(CANCELED);
    }

    @Transactional
    public void cancelAll(Collection<MeetJpa> cancellable, boolean forced) {
        cancellable.forEach(meet -> meetOperationsValidator.validateCancellation(meet, forced));
        Set<Long> cancellableIds = mapToSet(cancellable, SequencedPersistable::getId);
        cancellable.forEach(meet -> {
            Set<UserJpa> dissolvedMembers = meet.dissolve();
            notifyAll(meet, dissolvedMembers, MEET_CANCELLATION);
            meet.setStatus(CANCELED);
            meet.getRequests().clear();
        });
        meetRepository.deleteAllByUpdatableTargetId(cancellableIds);
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
        UserJpa joiner = userResourceService.fetchUserJpa(joinerId);
        meet.addMember(joiner);
    }

    public void joinViaRequest(MeetJpa meet, UserJpa joiner) {
        RestrictionsJpa restrictions = meet.getRestrictions();
        meetOperationsValidator.validateJoin(meet, joiner.getId(), Availability.valueOf(restrictions.getAvailability()));
        meet.addMember(joiner);
    }

    public void leaveAll(List<MeetJpa> memberMeets, UserJpa leaver, boolean forced) {
        memberMeets.forEach(meet -> meetOperationsValidator.validateLeave(meet, leaver, forced));
        memberMeets.forEach(meet -> meet.removeMember(leaver));
    }
}
