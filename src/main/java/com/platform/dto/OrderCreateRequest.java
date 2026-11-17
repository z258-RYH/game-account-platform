package com.platform.dto;

/**
 * 下单请求
 */
public class OrderCreateRequest {
    private Long accountId;   // 要购买的账号 ID
    private Double price;      // 成交价格（议价后的价格）

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
