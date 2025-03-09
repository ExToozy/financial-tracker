package ru.extoozy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetEntity {

    private Long id;

    private YearMonth period;

    private BigDecimal maxAmount;

    private BigDecimal currentAmount;

    private UserProfileEntity userProfile;

}
