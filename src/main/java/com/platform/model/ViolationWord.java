package com.platform.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "violation_word")
public class ViolationWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;               // 违规词
    private String category;           // 分类
    private Integer level;             // 严重程度
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ViolationWord() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
