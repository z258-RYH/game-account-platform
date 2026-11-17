package com.platform.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.platform.model.ViolationWord;
import com.platform.repository.ViolationWordRepository;

@Service
public class ContentModerationService {
    @Autowired
    private ViolationWordRepository violationWordRepository;

    private static final Pattern PHONE_PATTERN = Pattern.compile(".*\\d{11}.*");
    private static final Pattern WECHAT_PATTERN = Pattern.compile(".*(vx|wechat|微信).*");
    private static final Pattern QQ_PATTERN = Pattern.compile(".*qq.*");

    public void validate(String text) {
        if (text == null || text.isEmpty()) return;
        String t = text.toLowerCase();

        // 检查违规词
        List<ViolationWord> violationWords = violationWordRepository.findAll();
        for (ViolationWord word : violationWords) {
            String keyword = word.getWord().toLowerCase().trim();
            if (!keyword.isEmpty() && t.contains(keyword)) {
                throw new IllegalArgumentException("内容包含违规词: " + word.getWord());
            }
        }

        // 检查联系方式
        if (PHONE_PATTERN.matcher(t).matches()) {
            throw new IllegalArgumentException("内容包含手机号等联系方式");
        }
        if (WECHAT_PATTERN.matcher(t).matches()) {
            throw new IllegalArgumentException("内容包含微信等违规联系方式");
        }
        if (QQ_PATTERN.matcher(t).matches()) {
            throw new IllegalArgumentException("内容包含QQ等违规联系方式");
        }

        // 检查虚假宣传词汇
        if (t.contains("最优惠") || t.contains("绝对安全") || t.contains("100%保证")) {
            throw new IllegalArgumentException("内容包含虚假宣传词汇");
        }
    }

    public List<ViolationWord> getViolationWords() {
        return violationWordRepository.findAll();
    }

    public ViolationWord addViolationWord(String word, String category, Integer level) {
        ViolationWord violationWord = new ViolationWord();
        violationWord.setWord(word);
        violationWord.setCategory(category);
        violationWord.setLevel(level);
        return violationWordRepository.save(violationWord);
    }

    public void deleteViolationWord(Long id) {
        violationWordRepository.deleteById(id);
    }

    public List<ViolationWord> getViolationWordsByCategory(String category) {
        return violationWordRepository.findByCategory(category);
    }

    public List<ViolationWord> getViolationWordsByLevel(Integer level) {
        return violationWordRepository.findByLevelGreaterThanEqual(level);
    }
}
