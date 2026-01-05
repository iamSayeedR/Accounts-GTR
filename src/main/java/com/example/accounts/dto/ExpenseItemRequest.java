package com.example.accounts.dto;

import com.example.accounts.entity.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseItemRequest {

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Expense item type is required")
    private Long expenseItemTypeId;

    @Builder.Default
    private ResourceType resourceType = ResourceType.ALL;
}
