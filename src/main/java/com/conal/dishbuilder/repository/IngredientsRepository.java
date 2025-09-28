package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.domain.IngredientsEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngredientsRepository extends JpaRepository<IngredientsEntity, UUID> {
    boolean existsByNameAndTenantId(@NonNull String name, @NonNull UUID tenantId);
}
