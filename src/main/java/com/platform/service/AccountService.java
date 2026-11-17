package com.platform.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.platform.model.Account;
import com.platform.repository.AccountRepository;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ContentModerationService contentModerationService;

    public Account publish(Account account, Long sellerId) {
        // 内容审核
        contentModerationService.validate(account.getGame());
        contentModerationService.validate(account.getDescription());
        if (account.getAssets() != null) {
            contentModerationService.validate(account.getAssets());
        }

        account.setSellerId(sellerId);
        account.setAuditStatus("PENDING"); // 默认待审核
        account.setViewCount(0);
        account.setFavoriteCount(0);
        return accountRepository.save(account);
    }

    public boolean audit(Long id, boolean pass, String message) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) return false;
        account.setAuditStatus(pass ? "APPROVED" : "REJECTED");
        account.setAuditMessage(message);
        account.setAuditedAt(LocalDateTime.now());
        accountRepository.save(account);
        return true;
    }

    public List<Account> list(String game, String server, Double minPrice, Double maxPrice) {
        return accountRepository.findByFilters(game, server, minPrice, maxPrice);
    }

    public List<Account> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return accountRepository.findByFilters(null, null, null, null);
        }
        return accountRepository.searchByKeyword(keyword.trim());
    }

    public Account getById(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            // 增加浏览次数
            account.setViewCount(account.getViewCount() + 1);
            accountRepository.save(account);
        }
        return account;
    }

    public List<Account> getBySellerId(Long sellerId) {
        return accountRepository.findBySellerId(sellerId);
    }

    public List<Account> getByAuditStatus(String auditStatus) {
        return accountRepository.findByAuditStatus(auditStatus);
    }

    public List<Account> getHotAccounts() {
        return accountRepository.findHotAccounts();
    }

    public List<Account> getCostEffectiveAccounts() {
        return accountRepository.findCostEffectiveAccounts();
    }

    public Account update(Long id, Account updatedAccount, Long sellerId) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null || !account.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("账号不存在或无权限修改");
        }

        // 内容审核
        if (updatedAccount.getGame() != null) {
            contentModerationService.validate(updatedAccount.getGame());
        }
        if (updatedAccount.getDescription() != null) {
            contentModerationService.validate(updatedAccount.getDescription());
        }
        if (updatedAccount.getAssets() != null) {
            contentModerationService.validate(updatedAccount.getAssets());
        }

        // 更新字段
        if (updatedAccount.getGame() != null) account.setGame(updatedAccount.getGame());
        if (updatedAccount.getServer() != null) account.setServer(updatedAccount.getServer());
        if (updatedAccount.getDescription() != null) account.setDescription(updatedAccount.getDescription());
        if (updatedAccount.getAssets() != null) account.setAssets(updatedAccount.getAssets());
        if (updatedAccount.getBindingStatus() != null) account.setBindingStatus(updatedAccount.getBindingStatus());
        if (updatedAccount.getImages() != null) account.setImages(updatedAccount.getImages());
        if (updatedAccount.getVideo() != null) account.setVideo(updatedAccount.getVideo());
        if (updatedAccount.getPrice() != null) account.setPrice(updatedAccount.getPrice());

        // 更新后重新审核
        account.setAuditStatus("PENDING");
        account.setAuditMessage(null);
        account.setAuditedAt(null);

        return accountRepository.save(account);
    }

    public void delete(Long id, Long sellerId) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null || !account.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException("账号不存在或无权限删除");
        }
        accountRepository.delete(account);
    }

    public void incrementFavoriteCount(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            account.setFavoriteCount(account.getFavoriteCount() + 1);
            accountRepository.save(account);
        }
    }

    public void decrementFavoriteCount(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null && account.getFavoriteCount() > 0) {
            account.setFavoriteCount(account.getFavoriteCount() - 1);
            accountRepository.save(account);
        }
    }
}
