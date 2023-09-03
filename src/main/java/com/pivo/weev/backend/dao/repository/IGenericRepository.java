package com.pivo.weev.backend.dao.repository;

import com.pivo.weev.backend.dao.model.common.Entity;
import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IGenericRepository<KEY extends Serializable, E extends Entity> extends JpaRepository<E, KEY>, JpaSpecificationExecutor<E> {

}
