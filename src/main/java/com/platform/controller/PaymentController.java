package com.platform.controller;

import com.platform.service.PaymentService;
import com.platform.service.PaymentService.PaymentInfo;
import com.platform.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<?> create(Authentication auth,
                                    @RequestParam Long orderId,
                                    @RequestParam(required = false) String method) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        try {
            PaymentInfo p = paymentService.createPayment(orderId, method);
            Map<String, Object> resp = new HashMap<>();
            resp.put("paymentId", p.paymentId);
            resp.put("method", p.method);
            resp.put("status", p.status);
            resp.put("payUrl", "/mock/" + p.method.toLowerCase() + "/qr/" + p.paymentId);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/notify/alipay")
    public ResponseEntity<?> notifyAlipay(@RequestParam String paymentId,
                                          @RequestParam boolean success) {
        boolean ok = paymentService.handleCallback(paymentId, success);
        return ok ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body(false);
    }

    @PostMapping("/notify/wechat")
    public ResponseEntity<?> notifyWechat(@RequestParam String paymentId,
                                          @RequestParam boolean success) {
        boolean ok = paymentService.handleCallback(paymentId, success);
        return ok ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body(false);
    }

    @GetMapping("/status/{paymentId}")
    public ResponseEntity<?> status(Authentication auth, @PathVariable String paymentId) {
        Long userId = AuthUtil.getCurrentUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).body("请先登录");
        }
        PaymentInfo p = paymentService.get(paymentId);
        return p == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(p);
    }
}
