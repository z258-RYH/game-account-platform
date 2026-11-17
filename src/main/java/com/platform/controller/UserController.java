package com.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.config.JwtUtil;
import com.platform.dto.ChangePasswordRequest;
import com.platform.dto.ChangeTradePasswordRequest;
import com.platform.dto.LoginByPhoneRequest;
import com.platform.dto.LoginRequest;
import com.platform.dto.LoginResponse;
import com.platform.dto.RegisterRequest;
import com.platform.dto.SetTradePasswordRequest;
import com.platform.dto.ThirdPartyLoginRequest;
import com.platform.dto.UpdateProfileRequest;
import com.platform.dto.UserProfileResponse;
import com.platform.dto.VerifyTradePasswordRequest;
import com.platform.model.User;
import com.platform.service.AnomalyMonitoringService;
import com.platform.service.DeviceService;
import com.platform.service.RateLimiterService;
import com.platform.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户相关接口
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private AnomalyMonitoringService anomalyMonitoringService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 注册 - 无需登录
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User user = userService.register(req);
            // 注册成功后直接返回用户信息，不返回密码
            UserProfileResponse resp = userService.toProfileResponse(user);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 登录 - 无需登录
     * 支持用户名或手机号登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        // 限流防护：每分钟最多允许 10 次尝试
        if (!rateLimiterService.allow("login:" + req.getUsername(), 10, 60000)) {
            return ResponseEntity.status(429).body("请求过于频繁，请稍后再试");
        }

        if (req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body("用户名和密码不能为空");
        }

        try {
            User user = userService.loginByUsernameOrPhone(req.getUsername(), req.getPassword());
            if (user == null) {
                return ResponseEntity.status(401).body("用户名或密码错误");
            }

            // 更新登录信息
            String clientIp = request.getRemoteAddr();
            userService.updateLoginInfo(user.getId(), clientIp);

            // 监控登录行为
            anomalyMonitoringService.monitorLogin(user.getId(), clientIp);

            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            LoginResponse resp = new LoginResponse(token, user.getId(), user.getUsername(), user.getVerified(), user.getRole());
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * 手机号+验证码登录 - 无需登录
     */
    @PostMapping("/login/phone")
    public ResponseEntity<?> loginByPhone(@RequestBody LoginByPhoneRequest req, HttpServletRequest request) {
        // 限流防护
        if (!rateLimiterService.allow("login:phone:" + req.getPhone(), 5, 60000)) {
            return ResponseEntity.status(429).body("请求过于频繁，请稍后再试");
        }

        if (req.getPhone() == null || req.getSmsCode() == null) {
            return ResponseEntity.badRequest().body("手机号和验证码不能为空");
        }

        try {
            User user = userService.loginByPhone(req.getPhone(), req.getSmsCode());
            if (user == null) {
                return ResponseEntity.status(401).body("验证码错误");
            }

            // 更新登录信息
            String clientIp = request.getRemoteAddr();
            userService.updateLoginInfo(user.getId(), clientIp);

            // 监控登录行为
            anomalyMonitoringService.monitorLogin(user.getId(), clientIp);

            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            LoginResponse resp = new LoginResponse(token, user.getId(), user.getUsername(), user.getVerified(), user.getRole());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 实名认证 - 需要登录
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verify(Authentication auth,
                                    @RequestParam String realName,
                                    @RequestParam String idCard) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        boolean ok = userService.verify(userId, realName, idCard);
        return ResponseEntity.ok(ok);
    }

    /**
     * 个人中心 - 需要登录
     * 可查看自己的 profile，或通过 id 查（后续可按权限限制）
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> profile(@PathVariable Long id) {
        UserProfileResponse resp = userService.toProfileResponse(userService.getProfile(id));
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    /**
     * 获取当前登录用户信息 - 需要登录
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        UserProfileResponse resp = userService.toProfileResponse(userService.getProfile(userId));
        return ResponseEntity.ok(resp);
    }

    /**
     * 更新个人资料 - 需要登录
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication auth,
                                          @RequestBody UpdateProfileRequest req) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        try {
            User user = userService.updateProfile(userId, req.getUsername(), req.getEmail(), req.getAvatarUrl());
            UserProfileResponse resp = userService.toProfileResponse(user);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 修改密码 - 需要登录
     */
    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(Authentication auth,
                                           @RequestBody ChangePasswordRequest req) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        try {
            userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
            return ResponseEntity.ok("密码修改成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 设置交易密码 - 需要登录
     */
    @PostMapping("/trade-password/set")
    public ResponseEntity<?> setTradePassword(Authentication auth,
                                             @RequestBody SetTradePasswordRequest req) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        try {
            userService.setTradePassword(userId, req.getTradePassword());
            return ResponseEntity.ok("交易密码设置成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 修改交易密码 - 需要登录
     */
    @PostMapping("/trade-password/change")
    public ResponseEntity<?> changeTradePassword(Authentication auth,
                                                @RequestBody ChangeTradePasswordRequest req) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        try {
            userService.changeTradePassword(userId, req.getOldTradePassword(), req.getNewTradePassword());
            return ResponseEntity.ok("交易密码修改成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 验证交易密码 - 需要登录
     */
    @PostMapping("/trade-password/verify")
    public ResponseEntity<?> verifyTradePassword(Authentication auth,
                                                @RequestBody VerifyTradePasswordRequest req) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        boolean valid = userService.verifyTradePassword(userId, req.getTradePassword());
        return ResponseEntity.ok(valid);
    }

    /**
     * 第三方平台登录/注册 - 无需登录
     */
    @PostMapping("/login/third-party")
    public ResponseEntity<?> loginByThirdParty(@RequestBody ThirdPartyLoginRequest req, HttpServletRequest request) {
        if (req.getThirdPartyType() == null || req.getThirdPartyId() == null) {
            return ResponseEntity.badRequest().body("第三方平台类型和用户ID不能为空");
        }

        try {
            // 注册或登录第三方用户
            User user = userService.registerByThirdParty(
                req.getThirdPartyType(),
                req.getThirdPartyId(),
                req.getNickname(),
                req.getAvatarUrl()
            );

            // 更新登录信息
            String clientIp = request.getRemoteAddr();
            userService.updateLoginInfo(user.getId(), clientIp);

            // 监控登录行为
            anomalyMonitoringService.monitorLogin(user.getId(), clientIp);

            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            LoginResponse resp = new LoginResponse(token, user.getId(), user.getUsername(), user.getVerified(), user.getRole());
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * 获取用户的设备列表 - 需要登录
     */
    @GetMapping("/devices")
    public ResponseEntity<?> getDevices(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(deviceService.getUserDevices(userId));
    }

    /**
     * 下线指定设备 - 需要登录
     */
    @PostMapping("/devices/{deviceId}/logout")
    public ResponseEntity<?> logoutDevice(Authentication auth, @PathVariable Long deviceId) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        boolean success = deviceService.logoutDevice(deviceId, userId);
        return ResponseEntity.ok(success);
    }

    /**
     * 下线所有设备 - 需要登录
     */
    @PostMapping("/devices/logout-all")
    public ResponseEntity<?> logoutAllDevices(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("请先登录");
        }
        Long userId = (Long) auth.getPrincipal();
        deviceService.logoutAllDevices(userId);
        return ResponseEntity.ok("所有设备已下线");
    }

    /**
     * 创建管理员账号（仅用于初始化）
     */
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin() {
        try {
            User admin = userService.createAdmin();
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
