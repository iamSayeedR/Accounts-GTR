package com.example.accounts.dto;

import com.example.accounts.entity.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    private Long itemId;
    private String code;
    private String description;
    private String fullDescription;
    private ItemType itemType;
    private String folder;
    private String category;
    private String manufacturer;
    private String brand;
    private String sku;
    private String hsCode;
    private String unitOfMeasure;
    private Integer processingTimeDays;
    private Boolean isActive;
    private ItemGLAccountResponse glAccounts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
