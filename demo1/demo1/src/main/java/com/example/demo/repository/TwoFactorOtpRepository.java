package com.example.demo.repository;

import com.example.demo.model.TwoFactorOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOtp, Long> {
    Optional<TwoFactorOtp> findTopByUsernameAndUserTypeOrderByCreatedAtDesc(String username, String userType);

    @Modifying
    @Transactional
    void deleteByUsernameAndUserType(String username, String userType);
}
