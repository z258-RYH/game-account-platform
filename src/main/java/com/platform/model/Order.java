package com.platform.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trade_order")  // order 是 SQL 保留字，改用 trade_order
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long buyerId;
    private Long sellerId;
    private Long accountId;
    private Double originalPrice;     // 原始价格
    private Double finalPrice;        // 最终价格（议价后）
    private Integer bargainRound = 0; // 议价轮数
    private String bargainInfo;       // 议价信息（JSON格式）
    private String status;            // PENDING,BARGAIN,ESCROW,DELIVERED,ACCEPTED,DISPUTED,CANCELLED
    private String paymentStatus = "UNPAID"; // 支付状态：UNPAID, PAID, REFUNDED
    private String paymentMethod;     // 支付方式
    private String paymentTransactionId; // 支付交易ID
    @Column(name = "paid_at")
    private LocalDateTime paidAt;     // 支付时间
    private String deliveryMethod;    // 交付方式：AUTO, MANUAL
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt; // 交付时间
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;  // 验收时间
    private Boolean dispute = false;   // 是否有纠纷
    private String disputeReason;      // 纠纷原因
    private String disputeStatus = "NONE"; // 纠纷状态：NONE, PENDING, RESOLVED
    @Column(name = "dispute_resolved_at")
    private LocalDateTime disputeResolvedAt; // 纠纷解决时间
    private String disputeResult;      // 纠纷结果
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Order() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public Double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(Double originalPrice) { this.originalPrice = originalPrice; }
    public Double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(Double finalPrice) { this.finalPrice = finalPrice; }
    public Integer getBargainRound() { return bargainRound; }
    public void setBargainRound(Integer bargainRound) { this.bargainRound = bargainRound; }
    public String getBargainInfo() { return bargainInfo; }
    public void setBargainInfo(String bargainInfo) { this.bargainInfo = bargainInfo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    public Boolean getDispute() { return dispute; }
    public void setDispute(Boolean dispute) { this.dispute = dispute; }
    public String getDisputeReason() { return disputeReason; }
    public void setDisputeReason(String disputeReason) { this.disputeReason = disputeReason; }
    public String getDisputeStatus() { return disputeStatus; }
    public void setDisputeStatus(String disputeStatus) { this.disputeStatus = disputeStatus; }
    public LocalDateTime getDisputeResolvedAt() { return disputeResolvedAt; }
    public void setDisputeResolvedAt(LocalDateTime disputeResolvedAt) { this.disputeResolvedAt = disputeResolvedAt; }
    public String getDisputeResult() { return disputeResult; }
    public void setDisputeResult(String disputeResult) { this.disputeResult = disputeResult; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
