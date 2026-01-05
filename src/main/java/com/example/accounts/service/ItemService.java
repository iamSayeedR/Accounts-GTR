package com.example.accounts.service;

import com.example.accounts.dto.ItemGLAccountResponse;
import com.example.accounts.dto.ItemRequest;
import com.example.accounts.dto.ItemResponse;
import com.example.accounts.entity.ChartOfAccount;
import com.example.accounts.entity.Item;
import com.example.accounts.entity.ItemGLAccount;
import com.example.accounts.entity.enums.ItemType;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.ChartOfAccountRepository;
import com.example.accounts.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;

    public ItemResponse createItem(ItemRequest request) {
        if (itemRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Item code already exists: " + request.getCode());
        }

        Item item = Item.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .fullDescription(request.getFullDescription())
                .itemType(request.getItemType())
                .folder(request.getFolder())
                .category(request.getCategory())
                .manufacturer(request.getManufacturer())
                .brand(request.getBrand())
                .sku(request.getSku())
                .hsCode(request.getHsCode())
                .unitOfMeasure(request.getUnitOfMeasure())
                .processingTimeDays(request.getProcessingTimeDays())
                .isActive(true)
                .build();

        // Create GL Account mappings if provided
        if (request.getGlAccounts() != null) {
            ItemGLAccount glAccount = createItemGLAccount(request.getGlAccounts());
            glAccount.setItem(item);
            item.setItemGLAccount(glAccount);
        }

        Item saved = itemRepository.save(item);
        return mapToResponse(saved);
    }

    public ItemResponse getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        return mapToResponse(item);
    }

    public ItemResponse getItemByCode(String code) {
        Item item = itemRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with code: " + code));
        return mapToResponse(item);
    }

    public List<ItemResponse> getItemsByType(ItemType itemType) {
        return itemRepository.findByItemType(itemType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getActiveItems() {
        return itemRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ItemResponse updateItem(Long itemId, ItemRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        if (!item.getCode().equals(request.getCode()) && itemRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Item code already exists: " + request.getCode());
        }

        item.setCode(request.getCode());
        item.setDescription(request.getDescription());
        item.setFullDescription(request.getFullDescription());
        item.setItemType(request.getItemType());
        item.setFolder(request.getFolder());
        item.setCategory(request.getCategory());
        item.setManufacturer(request.getManufacturer());
        item.setBrand(request.getBrand());
        item.setSku(request.getSku());
        item.setHsCode(request.getHsCode());
        item.setUnitOfMeasure(request.getUnitOfMeasure());
        item.setProcessingTimeDays(request.getProcessingTimeDays());

        // Update GL Account mappings
        if (request.getGlAccounts() != null) {
            if (item.getItemGLAccount() == null) {
                ItemGLAccount glAccount = createItemGLAccount(request.getGlAccounts());
                glAccount.setItem(item);
                item.setItemGLAccount(glAccount);
            } else {
                updateItemGLAccount(item.getItemGLAccount(), request.getGlAccounts());
            }
        }

        Item updated = itemRepository.save(item);
        return mapToResponse(updated);
    }

    public ItemResponse activateItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        item.setIsActive(true);
        Item updated = itemRepository.save(item);
        return mapToResponse(updated);
    }

    public ItemResponse deactivateItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        item.setIsActive(false);
        Item updated = itemRepository.save(item);
        return mapToResponse(updated);
    }

    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item not found with id: " + itemId);
        }
        itemRepository.deleteById(itemId);
    }

    private ItemGLAccount createItemGLAccount(com.example.accounts.dto.ItemGLAccountRequest request) {
        return ItemGLAccount.builder()
                .glAccount(getAccountById(request.getGlAccountId()))
                .goodsOnConsignmentAccount(getAccountById(request.getGoodsOnConsignmentAccountId()))
                .salesRevenueAccount(getAccountById(request.getSalesRevenueAccountId()))
                .tradeDiscountsAccount(getAccountById(request.getTradeDiscountsAccountId()))
                .costOfGoodsSoldAccount(getAccountById(request.getCostOfGoodsSoldAccountId()))
                .deferredExpensesAccount(getAccountById(request.getDeferredExpensesAccountId()))
                .outputVATAccount(getAccountById(request.getOutputVATAccountId()))
                .inputVATAccount(getAccountById(request.getInputVATAccountId()))
                .build();
    }

    private void updateItemGLAccount(ItemGLAccount glAccount, com.example.accounts.dto.ItemGLAccountRequest request) {
        glAccount.setGlAccount(getAccountById(request.getGlAccountId()));
        glAccount.setGoodsOnConsignmentAccount(getAccountById(request.getGoodsOnConsignmentAccountId()));
        glAccount.setSalesRevenueAccount(getAccountById(request.getSalesRevenueAccountId()));
        glAccount.setTradeDiscountsAccount(getAccountById(request.getTradeDiscountsAccountId()));
        glAccount.setCostOfGoodsSoldAccount(getAccountById(request.getCostOfGoodsSoldAccountId()));
        glAccount.setDeferredExpensesAccount(getAccountById(request.getDeferredExpensesAccountId()));
        glAccount.setOutputVATAccount(getAccountById(request.getOutputVATAccountId()));
        glAccount.setInputVATAccount(getAccountById(request.getInputVATAccountId()));
    }

    private ChartOfAccount getAccountById(Long accountId) {
        if (accountId == null)
            return null;
        return chartOfAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Chart of Account not found with id: " + accountId));
    }

    private ItemResponse mapToResponse(Item item) {
        ItemGLAccountResponse glAccountResponse = null;
        if (item.getItemGLAccount() != null) {
            ItemGLAccount gla = item.getItemGLAccount();
            glAccountResponse = ItemGLAccountResponse.builder()
                    .glAccountId(gla.getGlAccount() != null ? gla.getGlAccount().getAccountId() : null)
                    .glAccountCode(gla.getGlAccount() != null ? gla.getGlAccount().getAccountCode() : null)
                    .glAccountDescription(gla.getGlAccount() != null ? gla.getGlAccount().getDescription() : null)
                    .goodsOnConsignmentAccountId(gla.getGoodsOnConsignmentAccount() != null
                            ? gla.getGoodsOnConsignmentAccount().getAccountId()
                            : null)
                    .goodsOnConsignmentAccountCode(gla.getGoodsOnConsignmentAccount() != null
                            ? gla.getGoodsOnConsignmentAccount().getAccountCode()
                            : null)
                    .salesRevenueAccountId(
                            gla.getSalesRevenueAccount() != null ? gla.getSalesRevenueAccount().getAccountId() : null)
                    .salesRevenueAccountCode(
                            gla.getSalesRevenueAccount() != null ? gla.getSalesRevenueAccount().getAccountCode() : null)
                    .tradeDiscountsAccountId(
                            gla.getTradeDiscountsAccount() != null ? gla.getTradeDiscountsAccount().getAccountId()
                                    : null)
                    .tradeDiscountsAccountCode(
                            gla.getTradeDiscountsAccount() != null ? gla.getTradeDiscountsAccount().getAccountCode()
                                    : null)
                    .costOfGoodsSoldAccountId(
                            gla.getCostOfGoodsSoldAccount() != null ? gla.getCostOfGoodsSoldAccount().getAccountId()
                                    : null)
                    .costOfGoodsSoldAccountCode(
                            gla.getCostOfGoodsSoldAccount() != null ? gla.getCostOfGoodsSoldAccount().getAccountCode()
                                    : null)
                    .deferredExpensesAccountId(
                            gla.getDeferredExpensesAccount() != null ? gla.getDeferredExpensesAccount().getAccountId()
                                    : null)
                    .deferredExpensesAccountCode(
                            gla.getDeferredExpensesAccount() != null ? gla.getDeferredExpensesAccount().getAccountCode()
                                    : null)
                    .outputVATAccountId(
                            gla.getOutputVATAccount() != null ? gla.getOutputVATAccount().getAccountId() : null)
                    .outputVATAccountCode(
                            gla.getOutputVATAccount() != null ? gla.getOutputVATAccount().getAccountCode() : null)
                    .inputVATAccountId(
                            gla.getInputVATAccount() != null ? gla.getInputVATAccount().getAccountId() : null)
                    .inputVATAccountCode(
                            gla.getInputVATAccount() != null ? gla.getInputVATAccount().getAccountCode() : null)
                    .build();
        }

        return ItemResponse.builder()
                .itemId(item.getItemId())
                .code(item.getCode())
                .description(item.getDescription())
                .fullDescription(item.getFullDescription())
                .itemType(item.getItemType())
                .folder(item.getFolder())
                .category(item.getCategory())
                .manufacturer(item.getManufacturer())
                .brand(item.getBrand())
                .sku(item.getSku())
                .hsCode(item.getHsCode())
                .unitOfMeasure(item.getUnitOfMeasure())
                .processingTimeDays(item.getProcessingTimeDays())
                .isActive(item.getIsActive())
                .glAccounts(glAccountResponse)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
