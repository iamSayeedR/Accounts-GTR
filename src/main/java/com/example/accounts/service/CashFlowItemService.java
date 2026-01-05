package com.example.accounts.service;

import com.example.accounts.dto.CashFlowItemRequest;
import com.example.accounts.dto.CashFlowItemResponse;
import com.example.accounts.entity.CashFlowItem;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.CashFlowItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CashFlowItemService {

    private final CashFlowItemRepository cashFlowItemRepository;

    public CashFlowItemResponse createCashFlowItem(CashFlowItemRequest request) {
        if (cashFlowItemRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Cash flow item code already exists: " + request.getCode());
        }

        CashFlowItem cashFlowItem = CashFlowItem.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .isActive(true)
                .build();

        CashFlowItem saved = cashFlowItemRepository.save(cashFlowItem);
        return mapToResponse(saved);
    }

    public CashFlowItemResponse getCashFlowItemById(Long id) {
        CashFlowItem cashFlowItem = cashFlowItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash flow item not found with id: " + id));
        return mapToResponse(cashFlowItem);
    }

    public List<CashFlowItemResponse> getAllCashFlowItems() {
        return cashFlowItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CashFlowItemResponse updateCashFlowItem(Long id, CashFlowItemRequest request) {
        CashFlowItem cashFlowItem = cashFlowItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash flow item not found with id: " + id));

        if (!cashFlowItem.getCode().equals(request.getCode())
                && cashFlowItemRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Cash flow item code already exists: " + request.getCode());
        }

        cashFlowItem.setCode(request.getCode());
        cashFlowItem.setDescription(request.getDescription());

        CashFlowItem updated = cashFlowItemRepository.save(cashFlowItem);
        return mapToResponse(updated);
    }

    public void deleteCashFlowItem(Long id) {
        if (!cashFlowItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cash flow item not found with id: " + id);
        }
        cashFlowItemRepository.deleteById(id);
    }

    private CashFlowItemResponse mapToResponse(CashFlowItem cashFlowItem) {
        return CashFlowItemResponse.builder()
                .cashFlowItemId(cashFlowItem.getCashFlowItemId())
                .code(cashFlowItem.getCode())
                .description(cashFlowItem.getDescription())
                .isActive(cashFlowItem.getIsActive())
                .createdAt(cashFlowItem.getCreatedAt())
                .updatedAt(cashFlowItem.getUpdatedAt())
                .build();
    }
}
