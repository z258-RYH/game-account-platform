package com.platform.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);

    // 按买家ID查询
    List<Order> findByBuyerId(Long buyerId);
    // 按卖家ID查询
    List<Order> findBySellerId(Long sellerId);
    // 按账号ID查询
    List<Order> findByAccountId(Long accountId);
    // 按支付状态查询
    List<Order> findByPaymentStatus(String paymentStatus);
    // 按纠纷状态查询
    List<Order> findByDisputeStatus(String disputeStatus);
    // 按买家ID和状态查询
    List<Order> findByBuyerIdAndStatus(Long buyerId, String status);
    // 按卖家ID和状态查询
    List<Order> findBySellerIdAndStatus(Long sellerId, String status);
    // 查找需要自动处理的订单（例如超时未验收）
    @Query("SELECT o FROM Order o WHERE o.status = 'DELIVERED' AND o.acceptedAt IS NULL AND o.createdAt < :threshold")
    List<Order> findPendingAcceptanceOrders(@Param("threshold") LocalDateTime threshold);
    // 查找需要处理的纠纷订单
    @Query("SELECT o FROM Order o WHERE o.disputeStatus = 'PENDING'")
    List<Order> findPendingDisputeOrders();
}
