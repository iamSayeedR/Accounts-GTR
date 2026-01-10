package com.example.accounts.service;

import com.example.accounts.dto.ExpenseItemTypeRequest;
import com.example.accounts.dto.ExpenseItemTypeResponse;
import com.example.accounts.entity.ExpenseItemType;
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

    private final ExpenseItemTypeRepository repository;

    public ExpenseItemTypeResponse create(ExpenseItemTypeRequest request) {
        ExpenseItemType entity = ExpenseItemType.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .isActive(true)
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ExpenseItemTypeResponse getById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseItemType not found")));
    }

    @Transactional(readOnly = true)
    public List<ExpenseItemTypeResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ExpenseItemTypeResponse update(Long id, ExpenseItemTypeRequest request) {
        ExpenseItemType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseItemType not found"));
        entity.setCode(request.getCode());
        entity.setDescription(request.getDescription());
        return toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("ExpenseItemType not found");
        }
        repository.deleteById(id);
    }

    private ExpenseItemTypeResponse toResponse(ExpenseItemType entity) {
        return ExpenseItemTypeResponse.builder()
                .expenseItemTypeId(entity.getExpenseItemTypeId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
