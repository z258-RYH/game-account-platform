package com.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class DataSecurityService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 用于敏感信息加密的密钥（实际生产环境应从配置文件或密钥管理系统获取）
    private static final String SECRET_KEY = "your-secret-key-here";
    private static final String ALGORITHM = "AES";

    /**
     * 密码加密
     */
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 密码验证
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 敏感信息加密（如身份证号、银行卡号等）
     */
    public String encryptSensitiveInfo(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 敏感信息解密
     */
    public String decryptSensitiveInfo(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }

    /**
     * 身份证号脱敏
     */
    public String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) return idCard;
        return idCard.substring(0, 4) + "********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 手机号脱敏
     */
    public String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 姓名脱敏
     */
    public String maskRealName(String name) {
        if (name == null || name.isEmpty()) return "";
        if (name.length() == 1) return name;
        if (name.length() == 2) return name.charAt(0) + "*";
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }

    /**
     * 银行卡号脱敏
     */
    public String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) return bankCard;
        return bankCard.substring(0, 4) + " **** **** " + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * SQL注入防护：清理输入字符串
     */
    public String sanitizeSqlInput(String input) {
        if (input == null) return null;
        // 移除潜在的SQL注入字符
        return input.replaceAll("[';\\-\\/\\*]", "");
    }

    /**
     * XSS防护：清理HTML输入
     */
    public String sanitizeHtmlInput(String input) {
        if (input == null) return null;
        // 移除HTML标签
        return input.replaceAll("<[^>]*>", "");
    }
}
