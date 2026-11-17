package com.platform.dto;

/**
 * 验证交易密码请求体
 */
public class VerifyTradePasswordRequest {
    private String tradePassword;     // 交易密码

    public String getTradePassword() { return tradePassword; }
    public void setTradePassword(String tradePassword) { this.tradePassword = tradePassword; }
}
