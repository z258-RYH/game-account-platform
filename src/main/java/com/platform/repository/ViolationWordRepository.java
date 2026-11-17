package com.platform.repository;

import com.platform.model.ViolationWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationWordRepository extends JpaRepository<ViolationWord, Long> {
    List<ViolationWord> findByCategory(String category);
    List<ViolationWord> findByLevelGreaterThanEqual(Integer level);
}
