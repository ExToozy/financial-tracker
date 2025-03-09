package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;
import ru.extoozy.service.budget.BudgetService;

@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    public void create(CreateBudgetDto dto) {
        budgetService.create(dto);
    }

    public void update(UpdateBudgetDto dto) {
        budgetService.update(dto);
    }

    public BudgetDto getByUserProfileIdAndCurrentMonth(Long id) {
        return budgetService.getByUserProfileIdAndCurrentMonth(id);
    }
}
