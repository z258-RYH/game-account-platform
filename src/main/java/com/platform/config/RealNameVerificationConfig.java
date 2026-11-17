package com.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "realname")
public class RealNameVerificationConfig {
    private String apiKey;
    private String apiSecret;
    private String verifyUrl;
    private String faceVerifyUrl;
    private boolean sandbox;

    // getters and setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
    public String getVerifyUrl() { return verifyUrl; }
    public void setVerifyUrl(String verifyUrl) { this.verifyUrl = verifyUrl; }
    public String getFaceVerifyUrl() { return faceVerifyUrl; }
    public void setFaceVerifyUrl(String faceVerifyUrl) { this.faceVerifyUrl = faceVerifyUrl; }
    public boolean isSandbox() { return sandbox; }
    public void setSandbox(boolean sandbox) { this.sandbox = sandbox; }
}
