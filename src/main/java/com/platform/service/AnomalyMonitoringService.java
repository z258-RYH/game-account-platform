package com.platform.service;

import com.platform.model.AnomalyLog;
import com.platform.model.User;
import com.platform.model.Order;
import com.platform.repository.AnomalyLogRepository;
import com.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AnomalyMonitoringService {
    @Autowired
    private AnomalyLogRepository anomalyLogRepository;

    @Autowired
    private UserRepository userRepository;

    // 用于记录用户登录IP，检测异地登录
    private Map<Long, String> userLastLoginIp = new ConcurrentHashMap<>();

    // 用于记录用户交易频率，检测异常交易
    private Map<Long, Integer> userTradeCount = new ConcurrentHashMap<>();

    /**
     * 监控登录行为
     */
    public void monitorLogin(Long userId, String ipAddress) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        // 检查异地登录
        String lastIp = userLastLoginIp.get(userId);
        if (lastIp != null && !lastIp.equals(ipAddress)) {
            // 记录异地登录异常
            AnomalyLog log = new AnomalyLog();
            log.setUserId(userId);
            log.setType("LOGIN");
            log.setDescription("异地登录检测：从 " + lastIp + " 到 " + ipAddress);
            log.setIpAddress(ipAddress);
            log.setLevel(2); // 中等严重程度
            log.setActionTaken("触发二次验证");
            anomalyLogRepository.save(log);
        }

        // 更新最后登录IP
        userLastLoginIp.put(userId, ipAddress);
    }

    /**
     * 监控交易行为
     */
    public void monitorTrade(Long userId, Double amount) {
        // 检查交易频率
        int count = userTradeCount.getOrDefault(userId, 0) + 1;
        userTradeCount.put(userId, count);

        if (count > 5) { // 短时间内交易次数过多
            AnomalyLog log = new AnomalyLog();
            log.setUserId(userId);
            log.setType("TRADE");
            log.setDescription("交易频率异常：短时间内交易 " + count + " 次");
            log.setIpAddress("SYSTEM");
            log.setLevel(3); // 高严重程度
            log.setActionTaken("限制交易功能");
            anomalyLogRepository.save(log);
        }

        // 检查交易金额
        if (amount > 10000) { // 大额交易
            AnomalyLog log = new AnomalyLog();
            log.setUserId(userId);
            log.setType("TRADE");
            log.setDescription("大额交易检测：金额 " + amount + " 元");
            log.setIpAddress("SYSTEM");
            log.setLevel(2); // 中等严重程度
            log.setActionTaken("人工审核");
            anomalyLogRepository.save(log);
        }
    }

    /**
     * 监控资金行为
     */
    public void monitorPayment(Long userId, String paymentMethod, Double amount) {
        // 检查支付方式异常
        if ("ALIPAY".equals(paymentMethod) && amount > 5000) {
            AnomalyLog log = new AnomalyLog();
            log.setUserId(userId);
            log.setType("PAYMENT");
            log.setDescription("支付宝大额支付：" + amount + " 元");
            log.setIpAddress("SYSTEM");
            log.setLevel(2); // 中等严重程度
            log.setActionTaken("风险提示");
            anomalyLogRepository.save(log);
        }
    }

    /**
     * 监控内容行为
     */
    public void monitorContent(Long userId, String content) {
        // 检查内容长度异常
        if (content != null && content.length() > 1000) {
            AnomalyLog log = new AnomalyLog();
            log.setUserId(userId);
            log.setType("CONTENT");
            log.setDescription("内容过长：" + content.length() + " 字符");
            log.setIpAddress("SYSTEM");
            log.setLevel(1); // 低严重程度
            log.setActionTaken("内容审核");
            anomalyLogRepository.save(log);
        }
    }

    /**
     * 获取异常日志
     */
    public List<AnomalyLog> getAnomalyLogs() {
        return anomalyLogRepository.findAll();
    }

    /**
     * 获取未解决的异常
     */
    public List<AnomalyLog> getUnresolvedAnomalies() {
        return anomalyLogRepository.findByResolvedFalse();
    }

    /**
     * 标记异常为已解决
     */
    public void resolveAnomaly(Long logId) {
        AnomalyLog log = anomalyLogRepository.findById(logId).orElse(null);
        if (log != null) {
            log.setResolved(true);
            anomalyLogRepository.save(log);
        }
    }

    /**
     * 按类型获取异常
     */
    public List<AnomalyLog> getAnomaliesByType(String type) {
        return anomalyLogRepository.findByType(type);
    }

    /**
     * 按严重程度获取异常
     */
    public List<AnomalyLog> getAnomaliesByLevel(Integer level) {
        return anomalyLogRepository.findByLevelGreaterThanEqual(level);
    }
}
