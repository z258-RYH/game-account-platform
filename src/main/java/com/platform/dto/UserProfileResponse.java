package com.platform.dto;

/**
 * 个人中心返回数据（脱敏，不暴露敏感信息）
 */
public class UserProfileResponse {
    private Long id;
    private String username;
    private String phoneMasked;   // 脱敏：138****1234
    private String realNameMasked; // 脱敏：张**
    private Boolean verified;
    private String avatarUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhoneMasked() { return phoneMasked; }
    public void setPhoneMasked(String phoneMasked) { this.phoneMasked = phoneMasked; }
    public String getRealNameMasked() { return realNameMasked; }
    public void setRealNameMasked(String realNameMasked) { this.realNameMasked = realNameMasked; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
