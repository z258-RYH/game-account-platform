package com.platform.repository;

import com.platform.model.AnomalyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnomalyLogRepository extends JpaRepository<AnomalyLog, Long> {
    List<AnomalyLog> findByUserId(Long userId);
    List<AnomalyLog> findByType(String type);
    List<AnomalyLog> findByResolvedFalse();
    List<AnomalyLog> findByLevelGreaterThanEqual(Integer level);
}
