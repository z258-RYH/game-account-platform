package com.platform.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "account", indexes = {
    @Index(name = "idx_audit_status", columnList = "audit_status"),
    @Index(name = "idx_game", columnList = "game"),
    @Index(name = "idx_server", columnList = "server"),
    @Index(name = "idx_price", columnList = "price"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_view_count", columnList = "view_count")
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;            // 发布者用户 ID
    private String game;              // 游戏名称
    private String server;            // 区服
    private String description;      // 描述
    private String assets;            // 资产清单（JSON格式）
    private String bindingStatus;     // 绑定状态（手机、邮箱、实名等）
    private String images;            // 图片URL（逗号分隔）
    private String video;             // 视频URL
    private Double price;             // 价格
    private Integer viewCount = 0;    // 浏览次数
    private Integer favoriteCount = 0; // 收藏次数
    private String auditStatus = "PENDING"; // 审核状态：PENDING, APPROVED, REJECTED
    private String auditMessage;      // 审核消息
    @Column(name = "audited_at")
    private LocalDateTime auditedAt;  // 审核时间
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Account() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }
    public String getServer() { return server; }
    public void setServer(String server) { this.server = server; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAssets() { return assets; }
    public void setAssets(String assets) { this.assets = assets; }
    public String getBindingStatus() { return bindingStatus; }
    public void setBindingStatus(String bindingStatus) { this.bindingStatus = bindingStatus; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
    public String getAuditMessage() { return auditMessage; }
    public void setAuditMessage(String auditMessage) { this.auditMessage = auditMessage; }
    public LocalDateTime getAuditedAt() { return auditedAt; }
    public void setAuditedAt(LocalDateTime auditedAt) { this.auditedAt = auditedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
