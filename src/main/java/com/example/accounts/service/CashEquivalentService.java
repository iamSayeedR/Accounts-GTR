package com.example.accounts.service;

import com.example.accounts.dto.CashEquivalentRequest;
import com.example.accounts.dto.CashEquivalentResponse;
import com.example.accounts.entity.CashEquivalent;
import com.example.accounts.exception.ResourceNotFoundException;
import com.example.accounts.repository.CashEquivalentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CashEquivalentService {

    private final CashEquivalentRepository cashEquivalentRepository;

    public CashEquivalentResponse createCashEquivalent(CashEquivalentRequest request) {
        CashEquivalent cashEquivalent = CashEquivalent.builder()
                .description(request.getDescription())
                .price(request.getPrice())
                .currency(request.getCurrency())
                .groupName(request.getGroupName())
                .isActive(true)
                .build();

        CashEquivalent saved = cashEquivalentRepository.save(cashEquivalent);
        return mapToResponse(saved);
    }

    public CashEquivalentResponse getCashEquivalentById(Long id) {
        CashEquivalent cashEquivalent = cashEquivalentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash Equivalent not found with id: " + id));
        return mapToResponse(cashEquivalent);
    }

    public List<CashEquivalentResponse> getAllCashEquivalents() {
        return cashEquivalentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CashEquivalentResponse> getActiveCashEquivalents() {
        return cashEquivalentRepository.findByIsActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CashEquivalentResponse> getCashEquivalentsByGroup(String groupName) {
        return cashEquivalentRepository.findByGroupName(groupName).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CashEquivalentResponse updateCashEquivalent(Long id, CashEquivalentRequest request) {
        CashEquivalent cashEquivalent = cashEquivalentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash Equivalent not found with id: " + id));

        cashEquivalent.setDescription(request.getDescription());
        cashEquivalent.setPrice(request.getPrice());
        cashEquivalent.setCurrency(request.getCurrency());
        cashEquivalent.setGroupName(request.getGroupName());

        CashEquivalent updated = cashEquivalentRepository.save(cashEquivalent);
        return mapToResponse(updated);
    }

    public CashEquivalentResponse activateCashEquivalent(Long id) {
        CashEquivalent cashEquivalent = cashEquivalentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash Equivalent not found with id: " + id));

        cashEquivalent.setIsActive(true);
        CashEquivalent updated = cashEquivalentRepository.save(cashEquivalent);
        return mapToResponse(updated);
    }

    public CashEquivalentResponse deactivateCashEquivalent(Long id) {
        CashEquivalent cashEquivalent = cashEquivalentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash Equivalent not found with id: " + id));

        cashEquivalent.setIsActive(false);
        CashEquivalent updated = cashEquivalentRepository.save(cashEquivalent);
        return mapToResponse(updated);
    }

    public void deleteCashEquivalent(Long id) {
        if (!cashEquivalentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cash Equivalent not found with id: " + id);
        }
        cashEquivalentRepository.deleteById(id);
    }

    private CashEquivalentResponse mapToResponse(CashEquivalent cashEquivalent) {
        return CashEquivalentResponse.builder()
                .cashEquivalentId(cashEquivalent.getCashEquivalentId())
                .description(cashEquivalent.getDescription())
                .price(cashEquivalent.getPrice())
                .currency(cashEquivalent.getCurrency())
                .groupName(cashEquivalent.getGroupName())
                .isActive(cashEquivalent.getIsActive())
                .createdAt(cashEquivalent.getCreatedAt())
                .updatedAt(cashEquivalent.getUpdatedAt())
                .build();
    }
}
