package com.platform.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "anomaly_log")
public class AnomalyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;              // 关联用户ID
    private String type;              // 异常类型：LOGIN, TRADE, PAYMENT, CONTENT
    private String description;       // 异常描述
    private String ipAddress;         // 触发IP
    private Integer level;            // 严重程度
    private String actionTaken;       // 采取的措施
    private Boolean resolved = false; // 是否已解决
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AnomalyLog() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
    public Boolean getResolved() { return resolved; }
    public void setResolved(Boolean resolved) { this.resolved = resolved; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
