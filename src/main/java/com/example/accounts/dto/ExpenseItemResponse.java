package com.example.accounts.dto;

import com.example.accounts.entity.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseItemResponse {
    private Long expenseItemId;
    private String description;
    private Long expenseItemTypeId;
    private String expenseItemTypeCode;
    private String expenseItemTypeDescription;
    private ResourceType resourceType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
