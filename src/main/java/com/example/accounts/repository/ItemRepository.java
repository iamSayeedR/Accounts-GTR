package com.example.accounts.repository;

import com.example.accounts.entity.Item;
import com.example.accounts.entity.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByCode(String code);

    List<Item> findByItemType(ItemType itemType);

    List<Item> findByIsActive(Boolean isActive);

    List<Item> findByCategory(String category);

    boolean existsByCode(String code);
}
