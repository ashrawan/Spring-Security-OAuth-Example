package com.demo.springcustomizedstarterexample.repository;

import com.demo.springcustomizedstarterexample.entities.UserEntity;
import com.demo.springcustomizedstarterexample.security.oauth.common.SecurityEnums;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    @EntityGraph(attributePaths = "roles", type = EntityGraph.EntityGraphType.LOAD)
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "u.email = :email and u.registeredProviderName = :registeredProviderName " +
            "and u.verificationCodeExpiresAt >= UTC_TIMESTAMP and u.verificationCode = :verificationCode")
    Optional<UserEntity> verifyAndRetrieveEmailVerificationRequestUser(@Param("email") String email,
                                                                       @Param("registeredProviderName") SecurityEnums.AuthProviderId registeredProviderName,
                                                                       @Param("verificationCode") String verificationCode);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "u.email = :email and u.registeredProviderName = :validProviderName " +
            "and u.verificationCodeExpiresAt >= UTC_TIMESTAMP and u.verificationCode = :verificationCode")
    Optional<UserEntity> verifyAndRetrieveForgotPasswordRequestUser(@Param("email") String email,
                                                                    @Param("validProviderName") SecurityEnums.AuthProviderId validProviderName,
                                                                    @Param("verificationCode") String verificationCode);

}
