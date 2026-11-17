package com.platform.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.platform.config.PaymentConfig;
import com.platform.model.Order;
import com.platform.repository.OrderRepository;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {
    public static class PaymentInfo {
        public String paymentId;
        public Long orderId;
        public String method; // ALIPAY | WECHAT
        public String status; // CREATED | SUCCESS | FAIL
        public long createdAt;
        public String payUrl;
    }

    private final Map<String, PaymentInfo> store = new ConcurrentHashMap<>();

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentConfig paymentConfig;

    public PaymentInfo createPayment(Long orderId, String method) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus()) && !"REVIEW".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单状态不支持支付");
        }
        PaymentInfo p = new PaymentInfo();
        p.paymentId = UUID.randomUUID().toString();
        p.orderId = orderId;
        p.method = method == null ? "ALIPAY" : method.toUpperCase();
        p.status = "CREATED";
        p.createdAt = Instant.now().toEpochMilli();

        // 生成支付链接
        if ("ALIPAY".equals(p.method)) {
            p.payUrl = generateAlipayUrl(p, order);
        } else if ("WECHAT".equals(p.method)) {
            p.payUrl = generateWechatUrl(p, order);
        } else {
            p.payUrl = "/mock/" + p.method.toLowerCase() + "/qr/" + p.paymentId;
        }

        store.put(p.paymentId, p);
        order.setStatus("PENDING");
        order.setPaymentTransactionId(p.paymentId);
        order.setPaymentMethod(p.method);
        orderRepository.save(order);
        return p;
    }

    public PaymentInfo get(String paymentId) {
        return store.get(paymentId);
    }

    public boolean handleCallback(String paymentId, boolean success) {
        PaymentInfo p = store.get(paymentId);
        if (p == null) return false;
        p.status = success ? "SUCCESS" : "FAIL";

        Order order = orderRepository.findById(p.orderId).orElse(null);
        if (order == null) return false;
        if (success) {
            order.setStatus("ESCROW"); // 支付成功即资金托管
            order.setPaymentStatus("PAID");
        } else {
            order.setStatus("PENDING");
            order.setPaymentStatus("UNPAID");
        }
        orderRepository.save(order);
        return true;
    }

    private String generateAlipayUrl(PaymentInfo payment, Order order) {
        // 实际项目中，这里应该使用支付宝SDK生成真实的支付链接
        // 这里为了演示，生成一个模拟链接
        if (paymentConfig.getAlipay() != null && paymentConfig.getAlipay().isSandbox()) {
            return "https://openapi.alipaydev.com/gateway.do?" +
                   "app_id=" + paymentConfig.getAlipay().getAppId() +
                   "&out_trade_no=" + payment.paymentId +
                   "&total_amount=" + order.getFinalPrice() +
                   "&subject=游戏账号交易" +
                   "&notify_url=" + paymentConfig.getAlipay().getNotifyUrl() +
                   "&return_url=" + paymentConfig.getAlipay().getReturnUrl();
        } else {
            return "https://openapi.alipay.com/gateway.do?" +
                   "app_id=" + (paymentConfig.getAlipay() != null ? paymentConfig.getAlipay().getAppId() : "test") +
                   "&out_trade_no=" + payment.paymentId +
                   "&total_amount=" + order.getFinalPrice() +
                   "&subject=游戏账号交易" +
                   "&notify_url=" + (paymentConfig.getAlipay() != null ? paymentConfig.getAlipay().getNotifyUrl() : "http://localhost:8080/api/pay/notify/alipay") +
                   "&return_url=" + (paymentConfig.getAlipay() != null ? paymentConfig.getAlipay().getReturnUrl() : "http://localhost:8080/profile");
        }
    }

    private String generateWechatUrl(PaymentInfo payment, Order order) {
        // 实际项目中，这里应该使用微信支付SDK生成真实的支付链接
        // 这里为了演示，生成一个模拟链接
        if (paymentConfig.getWechat() != null && paymentConfig.getWechat().isSandbox()) {
            return "https://api.mch.weixin.qq.com/sandboxnew/pay/unifiedorder?" +
                   "appid=" + paymentConfig.getWechat().getAppId() +
                   "&mch_id=" + paymentConfig.getWechat().getMchId() +
                   "&out_trade_no=" + payment.paymentId +
                   "&total_fee=" + (int)(order.getFinalPrice() * 100) +
                   "&body=游戏账号交易" +
                   "&notify_url=" + paymentConfig.getWechat().getNotifyUrl();
        } else {
            return "https://api.mch.weixin.qq.com/pay/unifiedorder?" +
                   "appid=" + (paymentConfig.getWechat() != null ? paymentConfig.getWechat().getAppId() : "test") +
                   "&mch_id=" + (paymentConfig.getWechat() != null ? paymentConfig.getWechat().getMchId() : "test") +
                   "&out_trade_no=" + payment.paymentId +
                   "&total_fee=" + (int)(order.getFinalPrice() * 100) +
                   "&body=游戏账号交易" +
                   "&notify_url=" + (paymentConfig.getWechat() != null ? paymentConfig.getWechat().getNotifyUrl() : "http://localhost:8080/api/pay/notify/wechat");
        }
    }
}
