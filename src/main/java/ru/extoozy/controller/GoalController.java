package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.dto.goal.GoalDto;
import ru.extoozy.service.goal.GoalService;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    public void create(CreateGoalDto dto) {
        goalService.create(dto);
    }

    public List<GoalDto> getAllByUserProfileId(Long id) {
        return goalService.getAllByUserProfileId(id);
    }

    public void delete(Long id) {
        goalService.delete(id);
    }

    public void replenish(Long goalId, BigDecimal amount) {
        goalService.replenish(goalId, amount);
    }

}
