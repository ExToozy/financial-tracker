package ru.extoozy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.GetBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;
import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.util.MapperConverter;

import java.util.List;
import java.util.Map;

@Mapper(uses = MapperConverter.class)
public interface BudgetMapper {

    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    BudgetEntity toEntity(CreateBudgetDto dto);

    BudgetEntity toEntity(UpdateBudgetDto dto);

    BudgetDto toDto(BudgetEntity entity);

    @Mapping(target = "maxAmount", source = "max_amount")
    CreateBudgetDto toCreateBudgetDto(Map<String, Object> jsonMap);

    @Mapping(target = "budgetId", source = "budget_id")
    @Mapping(target = "maxAmount", source = "max_amount")
    UpdateBudgetDto toUpdateBudgetDto(Map<String, Object> jsonMap);

    @Mapping(target = "userProfileId", source = "user_profile_id")
    GetBudgetDto toGetBudgetDto(Map<String, Object> jsonMap);

    List<BudgetDto> toDto(List<BudgetEntity> entities);

}
