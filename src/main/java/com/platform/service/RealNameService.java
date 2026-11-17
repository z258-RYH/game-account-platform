package com.platform.service;

import org.springframework.stereotype.Service;

@Service
public class RealNameService {
    public boolean verify(String realName, String idCard) {
        if (realName == null || realName.trim().isEmpty()) return false;
        // 模拟权威接口校验：身份证号长度需为 18 位，且姓名不含数字
        if (idCard == null || idCard.length() != 18) return false;
        if (realName.matches(".*\\d.*")) return false;

        // 模拟 99% 的成功率
        return Math.random() < 0.99;
    }
}
