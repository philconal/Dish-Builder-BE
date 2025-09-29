package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.domain.UserEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsernameAndTenantIdAndStatus(@NonNull String username, @NonNull UUID tenantId, @NonNull CommonStatus status);

    Optional<UserEntity> findByIdAndTenantId(@NonNull UUID id, @NonNull UUID tenantId);

    Optional<UserEntity> findByUsernameAndStatus(@NonNull String username, @NonNull CommonStatus status);

    boolean existsByUsernameAndTenantId(@NonNull String username, @NonNull UUID tenantId);

    boolean existsByEmailAndTenantId(@NonNull String email, @NonNull UUID tenantId);
}
