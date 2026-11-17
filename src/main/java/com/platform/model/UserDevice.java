package com.platform.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_devices")
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // 关联的用户ID

    @Column(name = "device_id")
    private String deviceId; // 设备唯一标识

    @Column(name = "device_name")
    private String deviceName; // 设备名称

    @Column(name = "ip_address")
    private String ipAddress; // 登录IP

    @Column(name = "login_time")
    private LocalDateTime loginTime; // 登录时间

    @Column(name = "last_active_time")
    private LocalDateTime lastActiveTime; // 最后活跃时间

    private Boolean active = true; // 设备是否活跃

    public UserDevice() {
        this.loginTime = LocalDateTime.now();
        this.lastActiveTime = LocalDateTime.now();
    }

    // getter 和 setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
    public LocalDateTime getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(LocalDateTime lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
