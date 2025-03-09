package ru.extoozy.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;
import ru.extoozy.entity.BudgetEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BudgetMapper {

    public static BudgetEntity toEntity(CreateBudgetDto dto) {
        return BudgetEntity.builder()
                .maxAmount(dto.getMaxAmount())
                .build();
    }

    public static BudgetEntity toEntity(UpdateBudgetDto dto) {
        return BudgetEntity.builder()
                .id(dto.getId())
                .maxAmount(dto.getMaxAmount())
                .build();
    }

    public static BudgetDto toDto(BudgetEntity entity) {
        return BudgetDto.builder()
                .id(entity.getId())
                .period(entity.getPeriod())
                .maxAmount(entity.getMaxAmount())
                .currentAmount(entity.getCurrentAmount())
                .build();
    }

    public static List<BudgetDto> toDto(List<BudgetEntity> entities) {
        return entities.stream()
                .map(BudgetMapper::toDto)
                .toList();
    }

}
