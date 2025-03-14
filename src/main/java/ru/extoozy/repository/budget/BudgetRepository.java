package ru.extoozy.repository.budget;

import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.repository.CrudRepository;

import java.util.List;

public interface BudgetRepository extends CrudRepository<BudgetEntity, Long> {

    List<BudgetEntity> fingAllByUserProfileId(Long id);

    BudgetEntity findByUserProfileIdAndCurrentMonth(Long id);

}
