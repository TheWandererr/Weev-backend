package com.pivo.weev.backend.domain.mapping.domain.decorator;

import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.DELETED;

import com.pivo.weev.backend.domain.mapping.domain.MeetMapper;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class MeetMapperDecorator implements MeetMapper {

    private final MeetMapper delegate;

    @Override
    public Meet map(MeetJpa source) {
        if (source.isDeleted()) {
            Meet meet = new Meet();
            meet.setStatus(DELETED.name());
            return meet;
        }
        return delegate.map(source);
    }
}
