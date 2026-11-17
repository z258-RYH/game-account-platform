package com.platform.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String phone;
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;      // 使用 BCrypt 加密后存储
    @Column(name = "trade_password_hash")
    private String tradePasswordHash; // 交易密码
    private String realName;          // 真实姓名（脱敏展示）
    @Column(name = "id_card")
    private String idCard;           // 身份证（脱敏存储）
    private Boolean verified = false; // 是否实名
    private String avatarUrl;
    private String lastLoginIp;       // 最后登录IP
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime; // 最后登录时间
    private Integer loginAttempts = 0; // 登录失败次数
    private Boolean locked = false;    // 账号是否锁定
    private String role = "user";       // 用户角色：user, admin
    @Column(name = "third_party_type")
    private String thirdPartyType;      // 第三方平台类型（如微信、QQ、微博等）
    @Column(name = "third_party_id")
    private String thirdPartyId;        // 第三方平台的用户ID
    @Column(name = "third_party_token")
    private String thirdPartyToken;     // 第三方平台的访问令牌
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    // getter 和 setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getTradePasswordHash() { return tradePasswordHash; }
    public void setTradePasswordHash(String tradePasswordHash) { this.tradePasswordHash = tradePasswordHash; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getLastLoginIp() { return lastLoginIp; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }
    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    public Integer getLoginAttempts() { return loginAttempts; }
    public void setLoginAttempts(Integer loginAttempts) { this.loginAttempts = loginAttempts; }
    public Boolean getLocked() { return locked; }
    public void setLocked(Boolean locked) { this.locked = locked; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getThirdPartyType() { return thirdPartyType; }
    public void setThirdPartyType(String thirdPartyType) { this.thirdPartyType = thirdPartyType; }
    public String getThirdPartyId() { return thirdPartyId; }
    public void setThirdPartyId(String thirdPartyId) { this.thirdPartyId = thirdPartyId; }
    public String getThirdPartyToken() { return thirdPartyToken; }
    public void setThirdPartyToken(String thirdPartyToken) { this.thirdPartyToken = thirdPartyToken; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
