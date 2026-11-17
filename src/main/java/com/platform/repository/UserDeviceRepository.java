package com.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.model.UserDevice;

import java.util.List;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    List<UserDevice> findByUserId(Long userId);
    List<UserDevice> findByUserIdAndActiveTrue(Long userId);
    UserDevice findByUserIdAndDeviceId(Long userId, String deviceId);
}
