package com.platform.dto;

/**
 * 第三方平台登录/注册请求
 */
public class ThirdPartyLoginRequest {
    private String thirdPartyType; // 第三方平台类型，如 "wechat", "qq", "weibo" 等
    private String thirdPartyId;   // 第三方平台的用户ID
    private String nickname;       // 第三方平台的用户昵称
    private String avatarUrl;      // 第三方平台的用户头像URL

    // getter 和 setter
    public String getThirdPartyType() {
        return thirdPartyType;
    }

    public void setThirdPartyType(String thirdPartyType) {
        this.thirdPartyType = thirdPartyType;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
