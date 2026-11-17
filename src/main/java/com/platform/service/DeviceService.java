package com.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.platform.model.UserDevice;
import com.platform.repository.UserDeviceRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceService {
    @Autowired
    private UserDeviceRepository userDeviceRepository;

    /**
     * 记录用户登录设备
     */
    public UserDevice recordDeviceLogin(Long userId, String deviceId, String deviceName, String ipAddress) {
        // 查找是否已存在该设备
        UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId);
        if (device != null) {
            // 更新设备信息
            device.setDeviceName(deviceName);
            device.setIpAddress(ipAddress);
            device.setLastActiveTime(LocalDateTime.now());
            device.setActive(true);
        } else {
            // 创建新设备记录
            device = new UserDevice();
            device.setUserId(userId);
            device.setDeviceId(deviceId);
            device.setDeviceName(deviceName);
            device.setIpAddress(ipAddress);
        }
        return userDeviceRepository.save(device);
    }

    /**
     * 获取用户的所有设备
     */
    public List<UserDevice> getUserDevices(Long userId) {
        return userDeviceRepository.findByUserId(userId);
    }

    /**
     * 获取用户的活跃设备
     */
    public List<UserDevice> getActiveUserDevices(Long userId) {
        return userDeviceRepository.findByUserIdAndActiveTrue(userId);
    }

    /**
     * 下线指定设备
     */
    public boolean logoutDevice(Long deviceId, Long userId) {
        UserDevice device = userDeviceRepository.findById(deviceId).orElse(null);
        if (device != null && device.getUserId().equals(userId)) {
            device.setActive(false);
            userDeviceRepository.save(device);
            return true;
        }
        return false;
    }

    /**
     * 下线用户的所有设备
     */
    public void logoutAllDevices(Long userId) {
        List<UserDevice> devices = userDeviceRepository.findByUserIdAndActiveTrue(userId);
        for (UserDevice device : devices) {
            device.setActive(false);
        }
        userDeviceRepository.saveAll(devices);
    }

    /**
     * 更新设备的最后活跃时间
     */
    public void updateDeviceActivity(String deviceId, Long userId) {
        UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId);
        if (device != null) {
            device.setLastActiveTime(LocalDateTime.now());
            userDeviceRepository.save(device);
        }
    }
}
