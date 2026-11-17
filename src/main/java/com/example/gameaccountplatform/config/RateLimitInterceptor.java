package com.example.gameaccountplatform.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RateLimitInterceptor implements HandlerInterceptor {

    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 5; // 最大请求数
    private static final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(1); // 时间窗口1分钟

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        String key = clientIp + ":" + request.getRequestURI();
        RateLimitInfo info = rateLimitMap.get(key);
        long now = System.currentTimeMillis();

        if (info == null) {
            rateLimitMap.put(key, new RateLimitInfo(now, 1));
            return true;
        }

        if (now - info.getStartTime() > TIME_WINDOW) {
            rateLimitMap.put(key, new RateLimitInfo(now, 1));
            return true;
        }

        if (info.getCount() >= MAX_REQUESTS) {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return false;
        }

        info.incrementCount();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    private static class RateLimitInfo {
        private final long startTime;
        private int count;

        public RateLimitInfo(long startTime, int count) {
            this.startTime = startTime;
            this.count = count;
        }

        public long getStartTime() {
            return startTime;
        }

        public int getCount() {
            return count;
        }

        public void incrementCount() {
            this.count++;
        }
    }
}
