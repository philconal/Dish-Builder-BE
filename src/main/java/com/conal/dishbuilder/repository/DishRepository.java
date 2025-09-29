package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.domain.DishEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface DishRepository extends JpaRepository<DishEntity, UUID> {
    boolean existsByNameAndTenantId(@NonNull String name, @NonNull UUID tenantId);
}
