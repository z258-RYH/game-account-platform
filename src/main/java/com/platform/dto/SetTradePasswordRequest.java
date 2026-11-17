package com.platform.dto;

/**
 * 设置交易密码请求体
 */
public class SetTradePasswordRequest {
    private String tradePassword;     // 交易密码

    public String getTradePassword() { return tradePassword; }
    public void setTradePassword(String tradePassword) { this.tradePassword = tradePassword; }
}
