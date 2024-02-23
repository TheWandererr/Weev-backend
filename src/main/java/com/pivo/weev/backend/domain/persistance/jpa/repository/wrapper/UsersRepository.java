package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.projection.MeetsStatisticsProjection;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IUsersRepository;
import org.springframework.stereotype.Component;

@Component
public class UsersRepository extends GenericRepository<Long, UserJpa, IUsersRepository> {

    public UsersRepository(IUsersRepository repository) {
        super(repository, ResourceName.USER);
    }

    public MeetsStatisticsProjection getMeetsStatistics(Long userId) {
        return repository.getMeetsStatistics(userId);
    }
}
