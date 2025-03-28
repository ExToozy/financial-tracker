package ru.extoozy.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBudgetDto {

    private Long id;

    private BigDecimal maxAmount;

    private BigDecimal currentAmount;
}
