package com.platform.repository;

import com.platform.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndAccountId(Long userId, Long accountId);
    void deleteByUserIdAndAccountId(Long userId, Long accountId);
}
