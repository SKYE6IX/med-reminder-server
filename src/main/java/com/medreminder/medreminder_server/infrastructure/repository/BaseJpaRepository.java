package com.medreminder.medreminder_server.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;

public interface BaseJpaRepository<T, K>  extends CrudRepository<T, K> {
}
