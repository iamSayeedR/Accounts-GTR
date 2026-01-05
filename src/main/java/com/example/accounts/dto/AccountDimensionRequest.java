package com.example.accounts.dto;

import com.example.accounts.entity.enums.DimensionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDimensionRequest {

    @NotNull(message = "Dimension type is required")
    private DimensionType dimensionType;

    @Builder.Default
    private Boolean turnoversOnly = false;

    @Builder.Default
    private Boolean trackAmount = true;

    @Builder.Default
    private Boolean trackQuantitative = false;
}
