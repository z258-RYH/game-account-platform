package com.platform.controller;

import com.platform.model.ViolationWord;
import com.platform.model.AnomalyLog;
import com.platform.service.ContentModerationService;
import com.platform.service.AnomalyMonitoringService;
import com.platform.service.DataSecurityService;
import com.platform.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class SecurityController {
    @Autowired
    private ContentModerationService contentModerationService;

    @Autowired
    private AnomalyMonitoringService anomalyMonitoringService;

    @Autowired
    private DataSecurityService dataSecurityService;

    /**
     * 获取违规词列表
     */
    @GetMapping("/violation-words")
    public ResponseEntity<?> getViolationWords(Authentication auth) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        List<ViolationWord> words = contentModerationService.getViolationWords();
        return ResponseEntity.ok(words);
    }

    /**
     * 添加违规词
     */
    @PostMapping("/violation-words")
    public ResponseEntity<?> addViolationWord(Authentication auth, @RequestParam String word, @RequestParam String category, @RequestParam Integer level) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        ViolationWord violationWord = contentModerationService.addViolationWord(word, category, level);
        return ResponseEntity.ok(violationWord);
    }

    /**
     * 删除违规词
     */
    @DeleteMapping("/violation-words/{id}")
    public ResponseEntity<?> deleteViolationWord(Authentication auth, @PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        contentModerationService.deleteViolationWord(id);
        return ResponseEntity.ok("删除成功");
    }

    /**
     * 获取异常日志
     */
    @GetMapping("/anomalies")
    public ResponseEntity<?> getAnomalyLogs(Authentication auth) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        List<AnomalyLog> logs = anomalyMonitoringService.getAnomalyLogs();
        return ResponseEntity.ok(logs);
    }

    /**
     * 获取未解决的异常
     */
    @GetMapping("/anomalies/unresolved")
    public ResponseEntity<?> getUnresolvedAnomalies(Authentication auth) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        List<AnomalyLog> logs = anomalyMonitoringService.getUnresolvedAnomalies();
        return ResponseEntity.ok(logs);
    }

    /**
     * 标记异常为已解决
     */
    @PostMapping("/anomalies/{id}/resolve")
    public ResponseEntity<?> resolveAnomaly(Authentication auth, @PathVariable Long id) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        anomalyMonitoringService.resolveAnomaly(id);
        return ResponseEntity.ok("标记成功");
    }

    /**
     * 按类型获取异常
     */
    @GetMapping("/anomalies/type/{type}")
    public ResponseEntity<?> getAnomaliesByType(Authentication auth, @PathVariable String type) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        List<AnomalyLog> logs = anomalyMonitoringService.getAnomaliesByType(type);
        return ResponseEntity.ok(logs);
    }

    /**
     * 内容安全验证
     */
    @PostMapping("/validate-content")
    public ResponseEntity<?> validateContent(@RequestParam String content) {
        try {
            contentModerationService.validate(content);
            return ResponseEntity.ok("内容验证通过");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 敏感信息脱敏
     */
    @PostMapping("/mask-info")
    public ResponseEntity<?> maskInfo(@RequestParam String type, @RequestParam String value) {
        String maskedValue = "";
        switch (type) {
            case "idCard":
                maskedValue = dataSecurityService.maskIdCard(value);
                break;
            case "phone":
                maskedValue = dataSecurityService.maskPhone(value);
                break;
            case "realName":
                maskedValue = dataSecurityService.maskRealName(value);
                break;
            case "bankCard":
                maskedValue = dataSecurityService.maskBankCard(value);
                break;
            default:
                return ResponseEntity.badRequest().body("不支持的类型");
        }
        return ResponseEntity.ok(maskedValue);
    }
}
