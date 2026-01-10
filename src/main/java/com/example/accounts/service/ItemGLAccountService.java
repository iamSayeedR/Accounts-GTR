package com.example.accounts.service;

import com.example.accounts.dto.ItemGLAccountRequest;
import com.example.accounts.dto.ItemGLAccountResponse;
import com.example.accounts.entity.ChartOfAccount;
import com.example.accounts.entity.Item;
import com.example.accounts.entity.ItemGLAccount;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.ChartOfAccountRepository;
import com.example.accounts.repository.ItemGLAccountRepository;
import com.example.accounts.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemGLAccountService {

    private final ItemGLAccountRepository repository;
    private final ItemRepository itemRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;

    public ItemGLAccountResponse create(ItemGLAccountRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        ItemGLAccount entity = ItemGLAccount.builder()
                .item(item)
                .glAccount(getAccount(request.getGlAccountId()))
                .goodsOnConsignmentAccount(getAccount(request.getGoodsOnConsignmentAccountId()))
                .salesRevenueAccount(getAccount(request.getSalesRevenueAccountId()))
                .tradeDiscountsAccount(getAccount(request.getTradeDiscountsAccountId()))
                .costOfGoodsSoldAccount(getAccount(request.getCostOfGoodsSoldAccountId()))
                .deferredExpensesAccount(getAccount(request.getDeferredExpensesAccountId()))
                .outputVATAccount(getAccount(request.getOutputVATAccountId()))
                .inputVATAccount(getAccount(request.getInputVATAccountId()))
                .build();

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ItemGLAccountResponse getById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemGLAccount not found")));
    }

    @Transactional(readOnly = true)
    public List<ItemGLAccountResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ItemGLAccountResponse update(Long id, ItemGLAccountRequest request) {
        ItemGLAccount entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemGLAccount not found"));

        entity.setGlAccount(getAccount(request.getGlAccountId()));
        entity.setGoodsOnConsignmentAccount(getAccount(request.getGoodsOnConsignmentAccountId()));
        entity.setSalesRevenueAccount(getAccount(request.getSalesRevenueAccountId()));
        entity.setTradeDiscountsAccount(getAccount(request.getTradeDiscountsAccountId()));
        entity.setCostOfGoodsSoldAccount(getAccount(request.getCostOfGoodsSoldAccountId()));
        entity.setDeferredExpensesAccount(getAccount(request.getDeferredExpensesAccountId()));
        entity.setOutputVATAccount(getAccount(request.getOutputVATAccountId()));
        entity.setInputVATAccount(getAccount(request.getInputVATAccountId()));

        return toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("ItemGLAccount not found");
        }
        repository.deleteById(id);
    }

    private ChartOfAccount getAccount(Long id) {
        return id == null ? null
                : chartOfAccountRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private ItemGLAccountResponse toResponse(ItemGLAccount entity) {
        return ItemGLAccountResponse.builder()
                .itemGLAccountId(entity.getItemGLAccountId())
                .itemId(entity.getItem().getItemId())
                .itemCode(entity.getItem().getCode())
                .itemDescription(entity.getItem().getDescription())
                .glAccountId(entity.getGlAccount() != null ? entity.getGlAccount().getAccountId() : null)
                .glAccountCode(entity.getGlAccount() != null ? entity.getGlAccount().getAccountCode() : null)
                .glAccountDescription(entity.getGlAccount() != null ? entity.getGlAccount().getDescription() : null)
                .goodsOnConsignmentAccountId(entity.getGoodsOnConsignmentAccount() != null
                        ? entity.getGoodsOnConsignmentAccount().getAccountId()
                        : null)
                .goodsOnConsignmentAccountCode(entity.getGoodsOnConsignmentAccount() != null
                        ? entity.getGoodsOnConsignmentAccount().getAccountCode()
                        : null)
                .salesRevenueAccountId(
                        entity.getSalesRevenueAccount() != null ? entity.getSalesRevenueAccount().getAccountId() : null)
                .salesRevenueAccountCode(
                        entity.getSalesRevenueAccount() != null ? entity.getSalesRevenueAccount().getAccountCode()
                                : null)
                .tradeDiscountsAccountId(
                        entity.getTradeDiscountsAccount() != null ? entity.getTradeDiscountsAccount().getAccountId()
                                : null)
                .tradeDiscountsAccountCode(
                        entity.getTradeDiscountsAccount() != null ? entity.getTradeDiscountsAccount().getAccountCode()
                                : null)
                .costOfGoodsSoldAccountId(
                        entity.getCostOfGoodsSoldAccount() != null ? entity.getCostOfGoodsSoldAccount().getAccountId()
                                : null)
                .costOfGoodsSoldAccountCode(
                        entity.getCostOfGoodsSoldAccount() != null ? entity.getCostOfGoodsSoldAccount().getAccountCode()
                                : null)
                .deferredExpensesAccountId(
                        entity.getDeferredExpensesAccount() != null ? entity.getDeferredExpensesAccount().getAccountId()
                                : null)
                .deferredExpensesAccountCode(entity.getDeferredExpensesAccount() != null
                        ? entity.getDeferredExpensesAccount().getAccountCode()
                        : null)
                .outputVATAccountId(
                        entity.getOutputVATAccount() != null ? entity.getOutputVATAccount().getAccountId() : null)
                .outputVATAccountCode(
                        entity.getOutputVATAccount() != null ? entity.getOutputVATAccount().getAccountCode() : null)
                .inputVATAccountId(
                        entity.getInputVATAccount() != null ? entity.getInputVATAccount().getAccountId() : null)
                .inputVATAccountCode(
                        entity.getInputVATAccount() != null ? entity.getInputVATAccount().getAccountCode() : null)
                .build();
    }
}
