package com.pivo.weev.backend.domain.service;

import static com.pivo.weev.backend.common.utils.CollectionUtils.findFirst;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.SUBCATEGORY_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.jpa.model.common.ModerationStatus.ON_MODERATION;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.CloudResourceJpaMapper;
import com.pivo.weev.backend.domain.mapping.EventJpaMapper;
import com.pivo.weev.backend.domain.mapping.LocationJpaMapper;
import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.integration.client.cloudinary.model.Image;
import com.pivo.weev.backend.integration.service.cloudinary.CloudinaryService;
import com.pivo.weev.backend.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.jpa.model.event.CategoryJpa;
import com.pivo.weev.backend.jpa.model.event.EventJpa;
import com.pivo.weev.backend.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.jpa.model.event.SubcategoryJpa;
import com.pivo.weev.backend.jpa.repository.wrapper.EventCategoryRepositoryWrapper;
import com.pivo.weev.backend.jpa.repository.wrapper.EventRepositoryWrapper;
import com.pivo.weev.backend.jpa.repository.wrapper.LocationRepositoryWrapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsService {

  private final LocationRepositoryWrapper locationRepository;
  private final EventRepositoryWrapper eventRepository;
  private final EventCategoryRepositoryWrapper eventCategoryRepository;
  private final CloudinaryService cloudinaryService;

  @Transactional
  public void saveEvent(Event sample) {
    // validate date time and etc. TODO
    EventJpa jpaEvent = preparePersistableEvent(sample);
    eventRepository.save(jpaEvent);
  }

  private EventJpa preparePersistableEvent(Event sample) {
    EventJpa eventJpa = getMapper(EventJpaMapper.class).map(sample);
    eventJpa.setLocation(getLocation(sample));
    eventJpa.setCategory(retrieveCategory(sample));
    eventJpa.setSubcategory(retrieveSubcategory(eventJpa.getCategory(), sample));
    eventJpa.setPhoto(uploadPhoto(sample));
    eventJpa.setModerationStatus(ON_MODERATION);
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
