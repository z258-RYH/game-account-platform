package com.platform.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private static class Bucket {
        int count;
        long windowStart;
    }
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean allow(String key, int limit, long windowMs) {
        long now = Instant.now().toEpochMilli();
        Bucket b = buckets.computeIfAbsent(key, k -> new Bucket());
        if (b.windowStart == 0 || now - b.windowStart >= windowMs) {
            b.windowStart = now;
            b.count = 0;
        }
        if (b.count >= limit) {
            return false;
        }
        b.count++;
        return true;
    }
}
