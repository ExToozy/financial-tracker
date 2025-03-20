package ru.extoozy.service.budget.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;
import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.mapper.BudgetMapper;
import ru.extoozy.repository.budget.BudgetRepository;
import ru.extoozy.service.budget.BudgetService;

import java.math.BigDecimal;
import java.time.YearMonth;

@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    @Override
    public void create(CreateBudgetDto dto) {
        BudgetEntity budget = BudgetMapper.toEntity(dto);
        budget.setUserProfile(UserContext.getUser().getUserProfile());
        budget.setPeriod(YearMonth.now());
        budget.setCurrentAmount(BigDecimal.ZERO);
        budgetRepository.save(budget);
    }

    @Override
    public void update(UpdateBudgetDto dto) {
        BudgetEntity budget = budgetRepository.findByUserProfileIdAndCurrentMonth(UserContext.getUser().getUserProfile().getId());
        budget.setMaxAmount(dto.getMaxAmount());
        budget.setCurrentAmount(dto.getCurrentAmount());
        budget.setUserProfile(UserContext.getUser().getUserProfile());
        budgetRepository.update(budget);
    }

    @Override
    public BudgetDto getByUserProfileIdAndCurrentMonth(Long userProfileId) {
        BudgetEntity budget = budgetRepository.findByUserProfileIdAndCurrentMonth(userProfileId);
        return BudgetMapper.toDto(budget);
    }
}
