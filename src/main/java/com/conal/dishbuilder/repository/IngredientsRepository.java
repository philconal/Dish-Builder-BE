package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.domain.IngredientsEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface IngredientsRepository extends JpaRepository<IngredientsEntity, UUID> {
    boolean existsByNameAndTenantId(@NonNull String name, @NonNull UUID tenantId);
}
