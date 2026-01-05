package com.example.accounts.service;

import com.example.accounts.dto.ExpenseItemTypeRequest;
import com.example.accounts.dto.ExpenseItemTypeResponse;
import com.example.accounts.entity.ExpenseItemType;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.ExpenseItemTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseItemTypeService {

    private final ExpenseItemTypeRepository expenseItemTypeRepository;

    public ExpenseItemTypeResponse createExpenseItemType(ExpenseItemTypeRequest request) {
        if (expenseItemTypeRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Expense item type code already exists: " + request.getCode());
        }

        ExpenseItemType expenseItemType = ExpenseItemType.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .isActive(true)
                .build();

        ExpenseItemType saved = expenseItemTypeRepository.save(expenseItemType);
        return mapToResponse(saved);
    }

    public ExpenseItemTypeResponse getExpenseItemTypeById(Long id) {
        ExpenseItemType expenseItemType = expenseItemTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense item type not found with id: " + id));
        return mapToResponse(expenseItemType);
    }

    public List<ExpenseItemTypeResponse> getAllExpenseItemTypes() {
        return expenseItemTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ExpenseItemTypeResponse> getActiveExpenseItemTypes() {
        return expenseItemTypeRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ExpenseItemTypeResponse updateExpenseItemType(Long id, ExpenseItemTypeRequest request) {
        ExpenseItemType expenseItemType = expenseItemTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense item type not found with id: " + id));

        if (!expenseItemType.getCode().equals(request.getCode()) &&
                expenseItemTypeRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Expense item type code already exists: " + request.getCode());
        }

        expenseItemType.setCode(request.getCode());
        expenseItemType.setDescription(request.getDescription());

        ExpenseItemType updated = expenseItemTypeRepository.save(expenseItemType);
        return mapToResponse(updated);
    }

    public void deleteExpenseItemType(Long id) {
        if (!expenseItemTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense item type not found with id: " + id);
        }
        expenseItemTypeRepository.deleteById(id);
    }

    private ExpenseItemTypeResponse mapToResponse(ExpenseItemType expenseItemType) {
        return ExpenseItemTypeResponse.builder()
                .expenseItemTypeId(expenseItemType.getExpenseItemTypeId())
                .code(expenseItemType.getCode())
                .description(expenseItemType.getDescription())
                .isActive(expenseItemType.getIsActive())
                .createdAt(expenseItemType.getCreatedAt())
                .updatedAt(expenseItemType.getUpdatedAt())
                .build();
    }
}
