package com.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.platform.dto.RegisterRequest;
import com.platform.dto.UserProfileResponse;
import com.platform.model.User;
import com.platform.repository.UserRepository;

/**
 * 用户服务：注册、登录、实名、个人中心
 * 密码使用 BCrypt 加密存储
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginAttemptService loginAttemptService;

    public User register(RegisterRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码至少6位");
        }
        if (userRepository.findByUsername(req.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (req.getPhone() != null && !req.getPhone().isBlank()) {
            if (userRepository.findByPhone(req.getPhone()) != null) {
                throw new IllegalArgumentException("手机号已注册");
            }
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            if (userRepository.findByEmail(req.getEmail()) != null) {
                throw new IllegalArgumentException("邮箱已注册");
            }
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        return userRepository.save(user);
    }

    public User loginByPhone(String phone, String smsCode) {
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 这里应该验证短信验证码，暂时跳过
        return user;
    }

    public User updateProfile(Long userId, String username, String email, String avatarUrl) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (username != null && !username.isBlank()) {
            if (!username.equals(user.getUsername()) && userRepository.findByUsername(username) != null) {
                throw new IllegalArgumentException("用户名已存在");
            }
            user.setUsername(username);
        }
        if (email != null && !email.isBlank()) {
            if (!email.equals(user.getEmail()) && userRepository.findByEmail(email) != null) {
                throw new IllegalArgumentException("邮箱已注册");
            }
            user.setEmail(email);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        return userRepository.save(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("原密码错误");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码至少6位");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void setTradePassword(Long userId, String tradePassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (tradePassword.length() < 6) {
            throw new IllegalArgumentException("交易密码至少6位");
        }
        user.setTradePasswordHash(passwordEncoder.encode(tradePassword));
        userRepository.save(user);
    }

    public void changeTradePassword(Long userId, String oldTradePassword, String newTradePassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (user.getTradePasswordHash() == null) {
            throw new IllegalArgumentException("请先设置交易密码");
        }
        if (!passwordEncoder.matches(oldTradePassword, user.getTradePasswordHash())) {
            throw new IllegalArgumentException("原交易密码错误");
        }
        if (newTradePassword.length() < 6) {
            throw new IllegalArgumentException("新交易密码至少6位");
        }
        user.setTradePasswordHash(passwordEncoder.encode(newTradePassword));
        userRepository.save(user);
    }

    public boolean verifyTradePassword(Long userId, String tradePassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getTradePasswordHash() == null) {
            return false;
        }
        return passwordEncoder.matches(tradePassword, user.getTradePasswordHash());
    }

    public void updateLoginInfo(Long userId, String ipAddress) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setLastLoginIp(ipAddress);
            user.setLastLoginTime(java.time.LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public User loginByUsernameOrPhone(String usernameOrPhone, String password) {
        if (loginAttemptService.isLocked(usernameOrPhone)) {
            throw new RuntimeException("账号已被锁定，请30分钟后再试");
        }

        User user = userRepository.findByUsername(usernameOrPhone);
        if (user == null) {
            user = userRepository.findByPhone(usernameOrPhone);
        }

        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            loginAttemptService.recordFailure(usernameOrPhone);
            return null;
        }

        loginAttemptService.reset(usernameOrPhone);
        return user;
    }

    public boolean verify(Long userId, String realName, String idCard) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        user.setRealName(realName);
        user.setIdCard(maskIdCard(idCard));  // 脱敏存储：只存前4后4
        user.setVerified(true);
        userRepository.save(user);
        return true;
    }

    public User getProfile(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * 身份证脱敏：保留前4后4，中间用*替代
     */
    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) return idCard;
        return idCard.substring(0, 4) + "********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 手机号脱敏：138****1234
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 姓名脱敏：张** 或 张三*
     */
    public static String maskRealName(String name) {
        if (name == null || name.isEmpty()) return "";
        if (name.length() == 1) return name;
        if (name.length() == 2) return name.charAt(0) + "*";
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) return null;
        UserProfileResponse r = new UserProfileResponse();
        r.setId(user.getId());
        r.setUsername(user.getUsername());
        r.setPhoneMasked(maskPhone(user.getPhone()));
        r.setRealNameMasked(maskRealName(user.getRealName()));
        r.setVerified(Boolean.TRUE.equals(user.getVerified()));
        r.setAvatarUrl(user.getAvatarUrl());
        return r;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    /**
     * 第三方平台注册/登录
     * 如果用户已存在则直接登录，不存在则创建新用户
     */
    public User registerByThirdParty(String thirdPartyType, String thirdPartyId, String nickname, String avatarUrl) {
        // 查找是否已存在该第三方账号
        User user = userRepository.findByThirdPartyTypeAndThirdPartyId(thirdPartyType, thirdPartyId);
        if (user != null) {
            // 已存在，更新信息
            if (nickname != null && !nickname.isBlank()) {
                user.setUsername(nickname);
            }
            if (avatarUrl != null && !avatarUrl.isBlank()) {
                user.setAvatarUrl(avatarUrl);
            }
            return userRepository.save(user);
        }

        // 不存在，创建新用户
        user = new User();
        user.setUsername(nickname != null && !nickname.isBlank() ? nickname : "用户" + System.currentTimeMillis());
        user.setThirdPartyType(thirdPartyType);
        user.setThirdPartyId(thirdPartyId);
        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }

    /**
     * 第三方平台登录
     */
    public User loginByThirdParty(String thirdPartyType, String thirdPartyId) {
        return userRepository.findByThirdPartyTypeAndThirdPartyId(thirdPartyType, thirdPartyId);
    }

    /**
     * 创建管理员账号
     */
    public User createAdmin() {
        // 检查是否已存在管理员账号
        User existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin != null) {
            return existingAdmin;
        }

        // 创建新的管理员账号
        User admin = new User();
        admin.setUsername("admin");
        admin.setPhone("13800138000");
        admin.setEmail("admin@youhaoyi.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole("admin");
        admin.setVerified(true);

        return userRepository.save(admin);
    }
}
