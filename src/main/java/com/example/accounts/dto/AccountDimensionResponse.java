package com.example.accounts.dto;

import com.example.accounts.entity.enums.DimensionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDimensionResponse {
    private Long dimensionId;
    private DimensionType dimensionType;
    private Boolean turnoversOnly;
    private Boolean trackAmount;
    private Boolean trackQuantitative;
}
