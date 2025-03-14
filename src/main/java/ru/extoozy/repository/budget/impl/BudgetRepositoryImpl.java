package ru.extoozy.repository.budget.impl;

import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.budget.BudgetRepository;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BudgetRepositoryImpl implements BudgetRepository {

    private final HashMap<Long, BudgetEntity> budgets = new HashMap<>();

    private long currentId = 1L;

    @Override
    public void save(BudgetEntity budget) {
        budget.setId(currentId);
        budgets.put(currentId, budget);
        currentId += 1;
    }

    @Override
    public void update(BudgetEntity budget) {
        BudgetEntity budgetToUpdate = budgets.get(budget.getId());
        if (budgetToUpdate == null) {
            return;
        }
        if (budget.getCurrentAmount() != null) {
            budgetToUpdate.setCurrentAmount(budget.getCurrentAmount());
        }
        if (budget.getMaxAmount() != null) {
            budgetToUpdate.setMaxAmount(budget.getMaxAmount());
        }
    }

    @Override
    public boolean delete(Long id) {
        BudgetEntity budget = budgets.remove(id);
        return budget != null;
    }

    @Override
    public List<BudgetEntity> findAll() {
        return budgets.values().stream().toList();
    }

    @Override
    public BudgetEntity findById(Long id) {
        return Optional.ofNullable(budgets.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Budget with id=%s not found".formatted(id)));
    }

    @Override
    public List<BudgetEntity> fingAllByUserProfileId(Long id) {
        return budgets.values()
                .stream()
                .filter(budget -> budget.getUserProfile().getId().equals(id))
                .toList();
    }

    @Override
    public BudgetEntity findByUserProfileIdAndCurrentMonth(Long id) {
        return budgets.values()
                .stream()
                .filter(budget -> budget.getUserProfile().getId().equals(id))
                .filter(budget -> budget.getPeriod().equals(YearMonth.now()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Budget with id=%s and date=%s not found".formatted(id, YearMonth.now())
                ));
    }
}
