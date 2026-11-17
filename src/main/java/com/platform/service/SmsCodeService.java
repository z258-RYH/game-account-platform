package com.platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsCodeService {
    @Value("${login.sms.code-expiration-ms:300000}")
    private long expirationMs;

    private static class CodeInfo {
        String code;
        long expireAt;
    }

    private final Map<String, CodeInfo> codes = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String generate(String phone) {
        String code = String.format("%06d", random.nextInt(1000000));
        CodeInfo info = new CodeInfo();
        info.code = code;
        info.expireAt = Instant.now().toEpochMilli() + expirationMs;
        codes.put(phone, info);
        return code;
    }

    public boolean verify(String phone, String code) {
        CodeInfo info = codes.get(phone);
        if (info == null) return false;
        long now = Instant.now().toEpochMilli();
        if (now > info.expireAt) {
            codes.remove(phone);
            return false;
        }
        boolean ok = info.code.equals(code);
        if (ok) codes.remove(phone);
        return ok;
    }
}
