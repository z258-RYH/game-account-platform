package com.platform.dto;

/**
 * 修改密码请求体
 */
public class ChangePasswordRequest {
    private String oldPassword;     // 原密码
    private String newPassword;     // 新密码

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
