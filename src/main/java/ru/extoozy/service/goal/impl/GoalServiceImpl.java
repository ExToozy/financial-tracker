package ru.extoozy.service.goal.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.dto.goal.GoalDto;
import ru.extoozy.entity.GoalEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.enums.UserRole;
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
        GoalEntity goal = GoalMapper.INSTANCE.toEntity(dto);
        goal.setUserProfile(UserContext.getUser().getUserProfile());
        goal.setCurrentAmount(BigDecimal.ZERO);
        goalRepository.save(goal);
    }

    @Override
    public List<GoalDto> getAllByUserProfileId(Long userProfileId) {
        if (userDoesNotHaveAccessToUserProfile(UserContext.getUser(), userProfileId)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to user profile with id=%s".formatted(
                            UserContext.getUser().getId(),
                            userProfileId
                    )
            );
        }
        List<GoalEntity> goals = goalRepository.findAllByUserProfileId(userProfileId);
        return GoalMapper.INSTANCE.toDto(goals);
    }

    @Override
    public void delete(Long id) {
        GoalEntity goal = goalRepository.findById(id);
        if (userDoesNotHaveAccessToGoal(UserContext.getUser(), goal)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to goal with id=%s".formatted(
                            UserContext.getUser().getId(),
                            goal.getId()
                    )
            );
        }
        boolean deleted = goalRepository.delete(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Goal with id=%s not found".formatted(id));
        }
    }

    @Override
    public void replenish(Long goalId, BigDecimal amount) {
        GoalEntity goal = goalRepository.findById(goalId);
        if (userDoesNotHaveAccessToGoal(UserContext.getUser(), goal)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to goal with id=%s".formatted(
                            UserContext.getUser().getId(),
                            goal.getId()
                    )
            );
        }
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        goalRepository.update(goal);
    }

    private boolean userDoesNotHaveAccessToGoal(UserEntity user, GoalEntity goal) {
        return !user.getRole().equals(UserRole.ADMIN) &&
                !user.getUserProfile().getId().equals(goal.getUserProfile().getId());
    }

    private boolean userDoesNotHaveAccessToUserProfile(UserEntity user, Long userProfileId) {
        return !user.getRole().equals(UserRole.ADMIN) &&
                !user.getUserProfile().getId().equals(userProfileId);
    }
}
