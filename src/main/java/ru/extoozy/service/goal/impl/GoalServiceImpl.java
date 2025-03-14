package ru.extoozy.service.goal.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.dto.goal.GoalDto;
import ru.extoozy.entity.GoalEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.GoalMapper;
import ru.extoozy.repository.goal.GoalRepository;
import ru.extoozy.service.goal.GoalService;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;

    @Override
    public void create(CreateGoalDto dto) {
        GoalEntity goal = GoalMapper.toEntity(dto);
        goal.setUserProfile(UserContext.getUser().getUserProfile());
        goal.setCurrentAmount(BigDecimal.ZERO);
        goalRepository.save(goal);
    }

    @Override
    public List<GoalDto> getAllByUserProfileId(Long userProfileId) {
        List<GoalEntity> goals = goalRepository.findAllByUserProfileId(userProfileId);
        return GoalMapper.toDto(goals);
    }

    @Override
    public void delete(Long id) {
        boolean deleted = goalRepository.delete(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Goal with id=%s not found".formatted(id));
        }
    }

    @Override
    public void replenish(Long goalId, BigDecimal amount) {
        GoalEntity goal = goalRepository.findById(goalId);
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        goalRepository.update(goal);
    }
}
