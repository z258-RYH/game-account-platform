package com.platform.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.model.Account;
import com.platform.model.AnomalyLog;
import com.platform.model.Order;
import com.platform.model.User;
import com.platform.model.ViolationWord;
import com.platform.repository.AccountRepository;
import com.platform.repository.OrderRepository;
import com.platform.repository.UserRepository;
import com.platform.service.AccountService;
import com.platform.service.AnomalyMonitoringService;
import com.platform.service.ContentModerationService;
import com.platform.service.OrderService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ContentModerationService contentModerationService;
    @Autowired
    private AnomalyMonitoringService anomalyMonitoringService;
    @Autowired
    private OrderService orderService;

    /**
     * 订单管理：强制取消订单
     */
    @PostMapping("/order/cancel/{id}")
    public ResponseEntity<?> forceCancel(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        // 记录操作日志（模拟）
        System.out.println("管理员强制取消订单：" + id + "，时间：" + LocalDateTime.now());

        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return ResponseEntity.ok(true);
    }

    /**
     * 数据导出：订单数据
     */
    @GetMapping("/export/orders")
    public ResponseEntity<String> exportOrders() {
        // 模拟生成 CSV 内容
        StringBuilder csv = new StringBuilder("订单ID,买家ID,卖家ID,账号ID,原始价格,最终价格,状态,支付状态,创建时间\n");
        List<Order> all = orderRepository.findAll();
        for (Order o : all) {
            csv.append(o.getId()).append(",")
               .append(o.getBuyerId()).append(",")
               .append(o.getSellerId()).append(",")
               .append(o.getAccountId()).append(",")
               .append(o.getOriginalPrice()).append(",")
               .append(o.getFinalPrice()).append(",")
               .append(o.getStatus()).append(",")
               .append(o.getPaymentStatus()).append(",")
               .append(o.getCreatedAt()).append("\n");
        }
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=orders.csv")
                .body(csv.toString());
    }

    /**
     * 账号审核：获取待审核账号
     */
    @GetMapping("/accounts/pending")
    public List<Account> pendingAccounts() {
        return accountRepository.findByAuditStatus("PENDING");
    }

    /**
     * 账号审核：批量审核
     */
    @PostMapping("/accounts/audit/batch")
    public ResponseEntity<?> auditBatch(@RequestBody List<Long> ids, @RequestParam boolean pass, @RequestParam(required = false) String message) {
        if (ids == null || ids.isEmpty()) return ResponseEntity.badRequest().body("空ID列表");
        for (Long id : ids) {
            accountService.audit(id, pass, message);
        }
        return ResponseEntity.ok(true);
    }

    /**
     * 订单管理：查询订单
     */
    @GetMapping("/orders")
    public List<Order> orders(@RequestParam(required = false) String status,
                              @RequestParam(required = false) String start,
                              @RequestParam(required = false) String end) {
        if (start != null && end != null) {
            LocalDateTime s = LocalDateTime.parse(start);
            LocalDateTime e = LocalDateTime.parse(end);
            if (status != null && !status.isEmpty()) {
                return orderRepository.findByStatusAndCreatedAtBetween(status, s, e);
            }
            return orderRepository.findByCreatedAtBetween(s, e);
        }
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByStatus(status);
        }
        return orderRepository.findAll();
    }

    /**
     * 运营数据：核心指标
     */
    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        Map<String, Object> m = new HashMap<>();
        m.put("users", userRepository.count());
        m.put("accountsTotal", accountRepository.count());
        m.put("accountsPending", accountRepository.findByAuditStatus("PENDING").size());
        m.put("accountsApproved", accountRepository.findByAuditStatus("APPROVED").size());
        m.put("ordersTotal", orderRepository.count());
        m.put("ordersDisputed", orderRepository.findByStatus("DISPUTED").size());
        LocalDateTime now = LocalDateTime.now();
        m.put("ordersToday", orderRepository.findByCreatedAtBetween(now.minusDays(1), now).size());
        m.put("unresolvedAnomalies", anomalyMonitoringService.getUnresolvedAnomalies().size());
        return m;
    }

    /**
     * 违规词管理：获取违规词列表
     */
    @GetMapping("/moderation/words")
    public List<ViolationWord> getViolationWords() {
        return contentModerationService.getViolationWords();
    }

    /**
     * 违规词管理：添加违规词
     */
    @PostMapping("/moderation/words")
    public ResponseEntity<?> addViolationWord(@RequestParam String word, @RequestParam String category, @RequestParam Integer level) {
        ViolationWord violationWord = contentModerationService.addViolationWord(word, category, level);
        return ResponseEntity.ok(violationWord);
    }

    /**
     * 违规词管理：删除违规词
     */
    @DeleteMapping("/moderation/words/{id}")
    public ResponseEntity<?> deleteViolationWord(@PathVariable Long id) {
        contentModerationService.deleteViolationWord(id);
        return ResponseEntity.ok(true);
    }

    /**
     * 异常监控：获取异常日志
     */
    @GetMapping("/anomalies")
    public List<AnomalyLog> getAnomalies() {
        return anomalyMonitoringService.getAnomalyLogs();
    }

    /**
     * 异常监控：获取未解决的异常
     */
    @GetMapping("/anomalies/unresolved")
    public List<AnomalyLog> getUnresolvedAnomalies() {
        return anomalyMonitoringService.getUnresolvedAnomalies();
    }

    /**
     * 异常监控：标记异常为已解决
     */
    @PostMapping("/anomalies/{id}/resolve")
    public ResponseEntity<?> resolveAnomaly(@PathVariable Long id) {
        anomalyMonitoringService.resolveAnomaly(id);
        return ResponseEntity.ok(true);
    }

    /**
     * 纠纷管理：获取待处理的纠纷
     */
    @GetMapping("/disputes")
    public List<Order> getDisputes() {
        return orderService.getPendingDisputes();
    }

    /**
     * 纠纷管理：处理纠纷
     */
    @PostMapping("/disputes/{id}/resolve")
    public ResponseEntity<?> resolveDispute(@PathVariable Long id, @RequestParam String result) {
        boolean ok = orderService.resolveDispute(id, result, 1L); // 假设管理员ID为1
        return ResponseEntity.ok(ok);
    }

    /**
     * 用户管理：获取所有用户
     */
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * 用户管理：锁定/解锁用户
     */
    @PostMapping("/users/{id}/lock")
    public ResponseEntity<?> lockUser(@PathVariable Long id, @RequestParam boolean locked) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        user.setLocked(locked);
        userRepository.save(user);
        return ResponseEntity.ok(true);
    }
}
