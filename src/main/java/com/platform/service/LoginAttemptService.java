package com.platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    @Value("${security.lockout.max-failures:5}")
    private int maxFailures;
    @Value("${security.lockout.window-ms:900000}")
    private long windowMs;
    @Value("${security.lockout.duration-ms:1800000}")
    private long lockoutMs;

    private static class Info {
        int failures;
        long firstTs;
        long lockedUntil;
    }

    private final Map<String, Info> store = new ConcurrentHashMap<>();

    public boolean isLocked(String key) {
        // 暂时禁用账号锁定功能
        return false;
    }

    public void recordFailure(String key) {
        // 暂时禁用账号锁定功能
    }

    public void reset(String key) {
        store.remove(key);
    }
}
