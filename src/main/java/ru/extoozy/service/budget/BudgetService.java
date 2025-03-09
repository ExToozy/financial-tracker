package ru.extoozy.service.budget;

import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;

public interface BudgetService {

    void create(CreateBudgetDto dto);

    void update(UpdateBudgetDto dto);

    BudgetDto getByUserProfileIdAndCurrentMonth(Long userProfileId);

}
