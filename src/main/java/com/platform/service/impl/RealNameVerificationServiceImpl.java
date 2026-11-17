package com.platform.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.platform.config.RealNameVerificationConfig;
import com.platform.model.User;
import com.platform.service.RealNameVerificationService;
import com.platform.service.UserService;

@Service
public class RealNameVerificationServiceImpl implements RealNameVerificationService {

    @Autowired
    private UserService userService;

    @Autowired
    private RealNameVerificationConfig realNameConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean verifyIdentity(String name, String idCard) {
        if (realNameConfig.isSandbox()) {
            // 沙箱模式，模拟验证
            return name != null && idCard != null && idCard.length() == 18;
        }

        // 实际项目中，这里应该调用第三方实名认证接口
        try {
            Map<String, String> params = new HashMap<>();
            params.put("apiKey", realNameConfig.getApiKey());
            params.put("name", name);
            params.put("idCard", idCard);

            // 调用第三方接口
            Map<String, Object> response = restTemplate.postForObject(
                realNameConfig.getVerifyUrl(),
                params,
                Map.class
            );

            // 解析响应结果
            if (response != null && "success".equals(response.get("status"))) {
                return true;
            }
        } catch (Exception e) {
            // 调用失败，记录日志
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean verifyFace(String userId, String faceImageUrl) {
        if (realNameConfig.isSandbox()) {
            // 沙箱模式，模拟验证
            return userId != null && faceImageUrl != null;
        }

        // 实际项目中，这里应该调用人脸识别接口
        try {
            Map<String, String> params = new HashMap<>();
            params.put("apiKey", realNameConfig.getApiKey());
            params.put("userId", userId);
            params.put("faceImageUrl", faceImageUrl);

            // 调用第三方接口
            Map<String, Object> response = restTemplate.postForObject(
                realNameConfig.getFaceVerifyUrl(),
                params,
                Map.class
            );

            // 解析响应结果
            if (response != null && "success".equals(response.get("status"))) {
                return true;
            }
        } catch (Exception e) {
            // 调用失败，记录日志
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void updateUserRealNameStatus(User user, boolean verified) {
        user.setVerified(verified);
        userService.updateUser(user);
    }
}
