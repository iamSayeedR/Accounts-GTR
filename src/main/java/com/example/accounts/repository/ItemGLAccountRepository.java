package com.example.accounts.repository;

import com.example.accounts.entity.ItemGLAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemGLAccountRepository extends JpaRepository<ItemGLAccount, Long> {
    Optional<ItemGLAccount> findByItemItemId(Long itemId);

    boolean existsByItemItemId(Long itemId);
}
