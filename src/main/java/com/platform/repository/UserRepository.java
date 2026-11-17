package com.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhone(String phone);
    User findByUsername(String username);
    User findByEmail(String email);
    User findByPhoneOrEmailOrUsername(String phone, String email, String username);
    User findByThirdPartyTypeAndThirdPartyId(String thirdPartyType, String thirdPartyId);

    /**
     * 根据角色查找用户
     */
    List<User> findByRole(String role);
}
