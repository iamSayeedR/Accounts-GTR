package com.example.accounts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashEquivalentRequest {

    @NotBlank(message = "Description is required")
    private String description;

    private BigDecimal price;

    private String currency;

    private String groupName;
}
