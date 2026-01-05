package com.example.accounts.service;

import com.example.accounts.dto.IncomeItemRequest;
import com.example.accounts.dto.IncomeItemResponse;
import com.example.accounts.entity.IncomeItem;
import com.example.accounts.exception.DuplicateResourceException;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.IncomeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IncomeItemService {

    private final IncomeItemRepository incomeItemRepository;

    public IncomeItemResponse createIncomeItem(IncomeItemRequest request) {
        if (incomeItemRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Income item code already exists: " + request.getCode());
        }

        IncomeItem incomeItem = IncomeItem.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .isActive(true)
                .build();

        IncomeItem saved = incomeItemRepository.save(incomeItem);
        return mapToResponse(saved);
    }

    public IncomeItemResponse getIncomeItemById(Long id) {
        IncomeItem incomeItem = incomeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income item not found with id: " + id));
        return mapToResponse(incomeItem);
    }

    public List<IncomeItemResponse> getAllIncomeItems() {
        return incomeItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public IncomeItemResponse updateIncomeItem(Long id, IncomeItemRequest request) {
        IncomeItem incomeItem = incomeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income item not found with id: " + id));

        if (!incomeItem.getCode().equals(request.getCode()) && incomeItemRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Income item code already exists: " + request.getCode());
        }

        incomeItem.setCode(request.getCode());
        incomeItem.setDescription(request.getDescription());

        IncomeItem updated = incomeItemRepository.save(incomeItem);
        return mapToResponse(updated);
    }

    public void deleteIncomeItem(Long id) {
        if (!incomeItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Income item not found with id: " + id);
        }
        incomeItemRepository.deleteById(id);
    }

    private IncomeItemResponse mapToResponse(IncomeItem incomeItem) {
        return IncomeItemResponse.builder()
                .incomeItemId(incomeItem.getIncomeItemId())
                .code(incomeItem.getCode())
                .description(incomeItem.getDescription())
                .isActive(incomeItem.getIsActive())
                .createdAt(incomeItem.getCreatedAt())
                .updatedAt(incomeItem.getUpdatedAt())
                .build();
    }
}
