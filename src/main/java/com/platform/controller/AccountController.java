package com.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.model.Account;
import com.platform.service.AccountService;
import com.platform.service.AnomalyMonitoringService;
import com.platform.util.AuthUtil;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AnomalyMonitoringService anomalyMonitoringService;

    /**
     * 发布账号 - 需要登录，自动关联当前用户为 sellerId
     */
    @PostMapping("/publish")
    public ResponseEntity<?> publish(Authentication auth, @RequestBody Account account) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        if (account.getGame() == null || account.getGame().isBlank()) {
            return ResponseEntity.badRequest().body("游戏名称不能为空");
        }
        if (account.getPrice() == null || account.getPrice() <= 0) {
            return ResponseEntity.badRequest().body("价格必须大于0");
        }

        Account saved = accountService.publish(account, userId);
        // 监控内容行为
        if (account.getDescription() != null) {
            anomalyMonitoringService.monitorContent(userId, account.getDescription());
        }
        if (account.getAssets() != null) {
            anomalyMonitoringService.monitorContent(userId, account.getAssets());
        }
        return ResponseEntity.ok(saved);
    }

    /**
     * 审核账号 - 需要登录（后续可限制为管理员）
     */
    @PostMapping("/audit/{id}")
    public ResponseEntity<?> audit(@PathVariable Long id, @RequestParam boolean pass, @RequestParam(required = false) String message) {
        boolean ok = accountService.audit(id, pass, message);
        return ResponseEntity.ok(ok);
    }

    /**
     * 浏览账号 - 公开，支持多维度筛选
     */
    @GetMapping("/list")
    public List<Account> list(@RequestParam(required = false) String game,
                              @RequestParam(required = false) String server,
                              @RequestParam(required = false) Double minPrice,
                              @RequestParam(required = false) Double maxPrice) {
        return accountService.list(game, server, minPrice, maxPrice);
    }

    /**
     * 关键词搜索 - 公开
     */
    @GetMapping("/search")
    public List<Account> search(@RequestParam(required = false) String keyword) {
        return accountService.search(keyword);
    }

    /**
     * 获取热门账号 - 公开
     */
    @GetMapping("/hot")
    public List<Account> getHotAccounts() {
        return accountService.getHotAccounts();
    }

    /**
     * 获取性价比账号 - 公开
     */
    @GetMapping("/cost-effective")
    public List<Account> getCostEffectiveAccounts() {
        return accountService.getCostEffectiveAccounts();
    }

    /**
     * 获取当前用户发布的账号 - 需要登录
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyAccounts(Authentication auth) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        List<Account> accounts = accountService.getBySellerId(userId);
        return ResponseEntity.ok(accounts);
    }

    /**
     * 获取单个账号详情 - 公开
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Account account = accountService.getById(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    /**
     * 更新账号 - 需要登录，只能更新自己发布的账号
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, Authentication auth, @RequestBody Account account) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        try {
            Account updated = accountService.update(id, account, userId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 删除账号 - 需要登录，只能删除自己发布的账号
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        try {
            accountService.delete(id, userId);
            return ResponseEntity.ok("删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
