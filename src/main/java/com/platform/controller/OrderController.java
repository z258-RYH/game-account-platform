package com.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.dto.OrderCreateRequest;
import com.platform.model.Order;
import com.platform.service.AnomalyMonitoringService;
import com.platform.service.OrderService;
import com.platform.util.AuthUtil;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AnomalyMonitoringService anomalyMonitoringService;

    @PostMapping("/create")
    public ResponseEntity<?> create(Authentication auth, @RequestBody OrderCreateRequest req) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        if (req.getAccountId() == null) {
            return ResponseEntity.badRequest().body("账号ID不能为空");
        }
        try {
            Order order = orderService.create(userId, req.getAccountId(), req.getPrice());
            // 监控交易行为
            anomalyMonitoringService.monitorTrade(userId, order.getFinalPrice());
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bargain/{id}")
    public ResponseEntity<?> bargain(Authentication auth, @PathVariable Long id, @RequestParam Double price) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        try {
            Order order = orderService.bargain(id, price, userId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bargain/{id}/accept")
    public ResponseEntity<?> acceptBargain(Authentication auth, @PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        try {
            Order order = orderService.acceptBargain(id, userId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bargain/{id}/reject")
    public ResponseEntity<?> rejectBargain(Authentication auth, @PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        try {
            Order order = orderService.rejectBargain(id, userId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<?> pay(Authentication auth, @PathVariable Long id, @RequestParam String paymentMethod, @RequestParam String transactionId) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        // 获取订单信息以获取金额
        Order order = orderService.getById(id);
        boolean ok = orderService.pay(id, paymentMethod, transactionId, userId);
        if (ok && order != null) {
            // 监控支付行为
            anomalyMonitoringService.monitorPayment(userId, paymentMethod, order.getFinalPrice());
        }
        return ResponseEntity.ok(ok);
    }

    @PostMapping("/deliver/{id}")
    public ResponseEntity<?> deliver(Authentication auth, @PathVariable Long id, @RequestParam String deliveryMethod) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        boolean ok = orderService.deliver(id, deliveryMethod, userId);
        return ResponseEntity.ok(ok);
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<?> accept(Authentication auth, @PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        boolean ok = orderService.accept(id, userId);
        return ResponseEntity.ok(ok);
    }

    @PostMapping("/dispute/{id}")
    public ResponseEntity<?> dispute(Authentication auth, @PathVariable Long id, @RequestParam String reason) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        boolean ok = orderService.dispute(id, reason, userId);
        return ResponseEntity.ok(ok);
    }

    @PostMapping("/dispute/{id}/resolve")
    public ResponseEntity<?> resolveDispute(Authentication auth, @PathVariable Long id, @RequestParam String result) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        boolean ok = orderService.resolveDispute(id, result, userId);
        return ResponseEntity.ok(ok);
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(Authentication auth, @PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        boolean ok = orderService.cancel(id, userId);
        return ResponseEntity.ok(ok);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/my/buyer")
    public ResponseEntity<?> getMyBuyerOrders(Authentication auth) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        List<Order> orders = orderService.getByBuyerId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my/seller")
    public ResponseEntity<?> getMySellerOrders(Authentication auth) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        List<Order> orders = orderService.getBySellerId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/pending/disputes")
    public ResponseEntity<?> getPendingDisputes(Authentication auth) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        List<Order> orders = orderService.getPendingDisputes();
        return ResponseEntity.ok(orders);
    }
}
