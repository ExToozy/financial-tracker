package ru.extoozy.service.goal;

import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.dto.goal.GoalDto;

import java.math.BigDecimal;
import java.util.List;

public interface GoalService {
    void create(CreateGoalDto dto);

    List<GoalDto> getAllByUserProfileId(Long id);

    void delete(Long id);

    void replenish(Long goalId, BigDecimal amount);
}
