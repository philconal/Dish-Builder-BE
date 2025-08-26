package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.domain.UserEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsernameAndTenantId(@NonNull String username, @NonNull UUID tenantId);

    boolean existsByUsernameAndTenantId(@NonNull String username, @NonNull UUID tenantId);
    boolean existsByEmailAndTenantId(@NonNull String email, @NonNull UUID tenantId);
}
