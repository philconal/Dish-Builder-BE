package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.domain.CategoryEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByNameAndTenantId(@NonNull String name, @NonNull UUID tenantId);

    boolean existsByIdAndTenantId(UUID categoryId, UUID tenantId);
}
