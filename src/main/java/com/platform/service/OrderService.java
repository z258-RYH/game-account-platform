package com.platform.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.platform.model.Account;
import com.platform.model.Order;
import com.platform.repository.AccountRepository;
import com.platform.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Order create(Long buyerId, Long accountId, Double price) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            throw new IllegalArgumentException("账号不存在");
        }
        if (!"APPROVED".equals(account.getAuditStatus())) {
            throw new IllegalArgumentException("该账号未通过审核，无法购买");
        }

        // 高价值订单校验（例如 > 1000 元）
        if (account.getPrice() > 1000) {
            // 这里可以记录日志或触发管理员二次复审标志
        }

        if (price == null || price <= 0) {
            price = account.getPrice();
        }
        if (account.getSellerId().equals(buyerId)) {
            throw new IllegalArgumentException("不能购买自己发布的账号");
        }

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setSellerId(account.getSellerId());
        order.setAccountId(accountId);
        order.setOriginalPrice(account.getPrice());
        order.setFinalPrice(price);
        order.setStatus("PENDING");  // 待支付/待托管
        order.setPaymentStatus("UNPAID");
        return orderRepository.save(order);
    }

    public Order bargain(Long orderId, Double proposedPrice, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new IllegalArgumentException("只有买家可以发起议价");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalArgumentException("只能在待支付状态下议价");
        }
        if (order.getBargainRound() >= 3) {
            throw new IllegalArgumentException("议价次数已达上限");
        }

        order.setFinalPrice(proposedPrice);
        order.setBargainRound(order.getBargainRound() + 1);
        order.setStatus("BARGAIN");  // 议价中
        return orderRepository.save(order);
    }

    public Order acceptBargain(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!order.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("只有卖家可以接受议价");
        }
        if (!"BARGAIN".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单未处于议价状态");
        }

        order.setStatus("PENDING");  // 回到待支付状态
        return orderRepository.save(order);
    }

    public Order rejectBargain(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!order.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("只有卖家可以拒绝议价");
        }
        if (!"BARGAIN".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单未处于议价状态");
        }

        order.setFinalPrice(order.getOriginalPrice());
        order.setStatus("PENDING");  // 回到待支付状态
        return orderRepository.save(order);
    }

    public boolean pay(Long orderId, String paymentMethod, String transactionId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        if (!order.getBuyerId().equals(userId)) return false;
        if (!"PENDING".equals(order.getStatus())) return false;

        order.setPaymentStatus("PAID");
        order.setPaymentMethod(paymentMethod);
        order.setPaymentTransactionId(transactionId);
        order.setPaidAt(LocalDateTime.now());
        order.setStatus("ESCROW");  // 资金已托管
        orderRepository.save(order);
        return true;
    }

    public boolean deliver(Long orderId, String deliveryMethod, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        if (!order.getSellerId().equals(userId)) return false;
        if (!"ESCROW".equals(order.getStatus())) return false;

        order.setDeliveryMethod(deliveryMethod);
        order.setDeliveredAt(LocalDateTime.now());
        order.setStatus("DELIVERED");  // 已交付
        orderRepository.save(order);
        return true;
    }

    public boolean accept(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        if (!order.getBuyerId().equals(userId)) return false;
        if (!"DELIVERED".equals(order.getStatus())) return false;

        order.setAcceptedAt(LocalDateTime.now());
        order.setStatus("ACCEPTED");  // 买家确认验收
        orderRepository.save(order);
        return true;
    }

    public boolean dispute(Long orderId, String reason, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) return false;
        if (!"DELIVERED".equals(order.getStatus())) return false;

        order.setDispute(true);
        order.setDisputeReason(reason);
        order.setDisputeStatus("PENDING");
        order.setStatus("DISPUTED");  // 纠纷中
        orderRepository.save(order);
        return true;
    }

    public boolean resolveDispute(Long orderId, String result, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        if (!"DISPUTED".equals(order.getStatus())) return false;

        order.setDisputeStatus("RESOLVED");
        order.setDisputeResult(result);
        order.setDisputeResolvedAt(LocalDateTime.now());
        // 根据处理结果设置最终状态
        if ("REFUND".equals(result)) {
            order.setStatus("CANCELLED");
            order.setPaymentStatus("REFUNDED");
        } else {
            order.setStatus("ACCEPTED");
        }
        orderRepository.save(order);
        return true;
    }

    public boolean cancel(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) return false;
        if ("PAID".equals(order.getPaymentStatus())) return false; // 已支付的订单不能直接取消

        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return true;
    }

    public Order getById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<Order> getByBuyerId(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    public List<Order> getBySellerId(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }

    public List<Order> getByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getPendingDisputes() {
        return orderRepository.findPendingDisputeOrders();
    }

    public List<Order> getPendingAcceptanceOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(48);
        return orderRepository.findPendingAcceptanceOrders(threshold);
    }
}
