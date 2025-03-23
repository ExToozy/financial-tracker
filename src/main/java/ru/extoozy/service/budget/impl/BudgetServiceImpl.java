package ru.extoozy.service.budget.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;
import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
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
        BudgetEntity budget = BudgetMapper.INSTANCE.toEntity(dto);
        budget.setUserProfile(UserContext.getUser().getUserProfile());
        budget.setPeriod(YearMonth.now());
        budget.setCurrentAmount(BigDecimal.ZERO);
        budgetRepository.save(budget);
    }

    @Override
    public void update(UpdateBudgetDto dto) {
        BudgetEntity budget = budgetRepository.findById(dto.getBudgetId());
        if (userDoesNotHaveAccessToBudget(UserContext.getUser(), budget)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to budget with id=%s".formatted(
                            UserContext.getUser().getId(),
                            budget.getId()
                    )
            );
        }
        budget.setMaxAmount(dto.getMaxAmount());
        budget.setUserProfile(UserContext.getUser().getUserProfile());
        budgetRepository.update(budget);
    }

    @Override
    public BudgetDto getByUserProfileIdAndCurrentMonth(Long userProfileId) {
        BudgetEntity budget = budgetRepository.findByUserProfileIdAndCurrentMonth(userProfileId);
        if (userDoesNotHaveAccessToBudget(UserContext.getUser(), budget)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to budget with id=%s".formatted(
                            UserContext.getUser().getId(),
                            budget.getId()
                    )
            );
        }
        return BudgetMapper.INSTANCE.toDto(budget);
    }

    private boolean userDoesNotHaveAccessToBudget(UserEntity user, BudgetEntity budget) {
        return !user.getRole().equals(UserRole.ADMIN) &&
                !budget.getUserProfile().getId().equals(user.getUserProfile().getId());
    }
}
