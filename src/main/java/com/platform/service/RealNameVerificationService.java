package com.platform.service;

import com.platform.model.User;

public interface RealNameVerificationService {
    boolean verifyIdentity(String name, String idCard);
    boolean verifyFace(String userId, String faceImageUrl);
    void updateUserRealNameStatus(User user, boolean verified);
}
