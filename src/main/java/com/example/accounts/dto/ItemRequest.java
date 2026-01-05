package com.example.accounts.dto;

import com.example.accounts.entity.enums.ItemType;
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
public class ItemRequest {

    @NotBlank(message = "Item code is required")
    private String code;

    @NotBlank(message = "Description is required")
    private String description;

    private String fullDescription;

    @NotNull(message = "Item type is required")
    private ItemType itemType;

    private String folder;
    private String category;
    private String manufacturer;
    private String brand;
    private String sku;
    private String hsCode;

    @Builder.Default
    private String unitOfMeasure = "Pcs";

    private Integer processingTimeDays;

    private ItemGLAccountRequest glAccounts;
}
