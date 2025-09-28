package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.domain.DishEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DishRepository extends JpaRepository<DishEntity, UUID> {
    boolean existsByNameAndTenantId(@NonNull String name, @NonNull UUID tenantId);
}
