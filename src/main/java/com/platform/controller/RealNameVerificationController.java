package com.platform.controller;

import com.platform.model.User;
import com.platform.service.RealNameVerificationService;
import com.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/realname")
public class RealNameVerificationController {

    @Autowired
    private RealNameVerificationService realNameVerificationService;

    @Autowired
    private UserService userService;

    @PostMapping("/verify")
    public boolean verifyIdentity(Principal principal, @RequestParam String name, @RequestParam String idCard) {
        User user = userService.findByUsername(principal.getName());
        boolean verified = realNameVerificationService.verifyIdentity(name, idCard);
        if (verified) {
            user.setRealName(name);
            user.setIdCard(idCard);
            realNameVerificationService.updateUserRealNameStatus(user, true);
        }
        return verified;
    }

    @PostMapping("/face/verify")
    public boolean verifyFace(Principal principal, @RequestParam String faceImageUrl) {
        User user = userService.findByUsername(principal.getName());
        return realNameVerificationService.verifyFace(user.getId().toString(), faceImageUrl);
    }
}
