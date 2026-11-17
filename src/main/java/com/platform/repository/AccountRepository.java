package com.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * 多条件筛选：只返回已审核通过的账号
     */
    @Query("SELECT a FROM Account a WHERE a.auditStatus = 'APPROVED' " +
           "AND (:game IS NULL OR a.game = :game) " +
           "AND (:server IS NULL OR a.server = :server) " +
           "AND (:minPrice IS NULL OR a.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR a.price <= :maxPrice) " +
           "ORDER BY a.createdAt DESC")
    List<Account> findByFilters(@Param("game") String game,
                                @Param("server") String server,
                                @Param("minPrice") Double minPrice,
                                @Param("maxPrice") Double maxPrice);

    /**
     * 关键词搜索：游戏名、区服、描述中包含关键词
     */
    @Query("SELECT a FROM Account a WHERE a.auditStatus = 'APPROVED' " +
           "AND (LOWER(a.game) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.server) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY a.createdAt DESC")
    List<Account> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 按审核状态查询
     */
    List<Account> findByAuditStatus(String auditStatus);

    /**
     * 按卖家ID查询
     */
    List<Account> findBySellerId(Long sellerId);

    /**
     * 热门账号排序（按浏览量）
     */
    @Query("SELECT a FROM Account a WHERE a.auditStatus = 'APPROVED' ORDER BY a.viewCount DESC")
    List<Account> findHotAccounts();

    /**
     * 性价比排序（按价格和浏览量综合）
     */
    @Query("SELECT a FROM Account a WHERE a.auditStatus = 'APPROVED' AND a.price > 0 ORDER BY (a.viewCount / a.price) DESC")
    List<Account> findCostEffectiveAccounts();
}
