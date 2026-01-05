package com.example.accounts.service;

import com.example.accounts.dto.ExpenseItemRequest;
import com.example.accounts.dto.ExpenseItemResponse;
import com.example.accounts.entity.ExpenseItem;
import com.example.accounts.entity.ExpenseItemType;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.ExpenseItemRepository;
import com.example.accounts.repository.ExpenseItemTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseItemService {

    private final ExpenseItemRepository expenseItemRepository;
    private final ExpenseItemTypeRepository expenseItemTypeRepository;

    public ExpenseItemResponse createExpenseItem(ExpenseItemRequest request) {
        ExpenseItemType expenseItemType = expenseItemTypeRepository.findById(request.getExpenseItemTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense item type not found with id: " + request.getExpenseItemTypeId()));

        ExpenseItem expenseItem = ExpenseItem.builder()
                .description(request.getDescription())
                .expenseItemType(expenseItemType)
                .resourceType(request.getResourceType())
                .isActive(true)
                .build();

        ExpenseItem saved = expenseItemRepository.save(expenseItem);
        return mapToResponse(saved);
    }

    public ExpenseItemResponse getExpenseItemById(Long id) {
        ExpenseItem expenseItem = expenseItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense item not found with id: " + id));
        return mapToResponse(expenseItem);
    }

    public List<ExpenseItemResponse> getAllExpenseItems() {
        return expenseItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ExpenseItemResponse> getExpenseItemsByType(Long expenseItemTypeId) {
        return expenseItemRepository.findByExpenseItemTypeExpenseItemTypeId(expenseItemTypeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ExpenseItemResponse updateExpenseItem(Long id, ExpenseItemRequest request) {
        ExpenseItem expenseItem = expenseItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense item not found with id: " + id));

        ExpenseItemType expenseItemType = expenseItemTypeRepository.findById(request.getExpenseItemTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense item type not found with id: " + request.getExpenseItemTypeId()));

        expenseItem.setDescription(request.getDescription());
        expenseItem.setExpenseItemType(expenseItemType);
        expenseItem.setResourceType(request.getResourceType());

        ExpenseItem updated = expenseItemRepository.save(expenseItem);
        return mapToResponse(updated);
    }

    public void deleteExpenseItem(Long id) {
        if (!expenseItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense item not found with id: " + id);
        }
        expenseItemRepository.deleteById(id);
    }

    private ExpenseItemResponse mapToResponse(ExpenseItem expenseItem) {
        return ExpenseItemResponse.builder()
                .expenseItemId(expenseItem.getExpenseItemId())
                .description(expenseItem.getDescription())
                .expenseItemTypeId(expenseItem.getExpenseItemType().getExpenseItemTypeId())
                .expenseItemTypeCode(expenseItem.getExpenseItemType().getCode())
                .expenseItemTypeDescription(expenseItem.getExpenseItemType().getDescription())
                .resourceType(expenseItem.getResourceType())
                .isActive(expenseItem.getIsActive())
                .createdAt(expenseItem.getCreatedAt())
                .updatedAt(expenseItem.getUpdatedAt())
                .build();
    }
}
