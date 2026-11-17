package com.platform.dto;

/**
 * 手机号登录请求体
 */
public class LoginByPhoneRequest {
    private String phone;       // 手机号
    private String smsCode;     // 短信验证码

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSmsCode() { return smsCode; }
    public void setSmsCode(String smsCode) { this.smsCode = smsCode; }
}
