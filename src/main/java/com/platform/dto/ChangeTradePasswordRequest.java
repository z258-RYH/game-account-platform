package com.platform.dto;

/**
 * 修改交易密码请求体
 */
public class ChangeTradePasswordRequest {
    private String oldTradePassword;     // 原交易密码
    private String newTradePassword;     // 新交易密码

    public String getOldTradePassword() { return oldTradePassword; }
    public void setOldTradePassword(String oldTradePassword) { this.oldTradePassword = oldTradePassword; }
    public String getNewTradePassword() { return newTradePassword; }
    public void setNewTradePassword(String newTradePassword) { this.newTradePassword = newTradePassword; }
}
