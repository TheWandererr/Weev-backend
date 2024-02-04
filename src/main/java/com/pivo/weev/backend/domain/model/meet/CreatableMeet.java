package com.pivo.weev.backend.domain.model.meet;

import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.model.common.Identifiable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreatableMeet extends Identifiable {

    private String header;
    private String category;
    private String subcategory;
    private Location location;
    private int membersLimit;
    private String description;
    private MultipartFile photo;
    private boolean updatePhoto;
    private EntryFee entryFee;
    private Restrictions restrictions;
    private LocalDateTime localStartDateTime;
    private String startTimeZoneId;
    private LocalDateTime localEndDateTime;
    private String endTimeZoneId;
    private boolean saveAsTemplate;

    public boolean hasPhoto() {
        return nonNull(photo);
    }

}
