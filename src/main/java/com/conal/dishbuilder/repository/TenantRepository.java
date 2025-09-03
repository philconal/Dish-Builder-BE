package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.domain.TenantEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<TenantEntity, UUID>{
    boolean existsByUrlSlug(@NonNull String url);

    boolean existsByName(@NonNull String name);

    boolean existsByEmail(@NonNull String email);
    Optional<TenantEntity> findBySubDomainAndStatus(@NonNull String domain, @NonNull CommonStatus status);
}
