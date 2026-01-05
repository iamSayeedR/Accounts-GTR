package com.example.accounts.entity;

import com.example.accounts.entity.enums.DimensionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_dimensions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDimension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dimension_id")
    private Long dimensionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private ChartOfAccount chartOfAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "dimension_type", nullable = false)
    private DimensionType dimensionType;

    @Column(name = "turnovers_only", nullable = false)
    @Builder.Default
    private Boolean turnoversOnly = false;

    @Column(name = "track_amount", nullable = false)
    @Builder.Default
    private Boolean trackAmount = true;

    @Column(name = "track_quantitative", nullable = false)
    @Builder.Default
    private Boolean trackQuantitative = false;
}
