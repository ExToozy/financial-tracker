package ru.extoozy.repository.goal.impl;

import ru.extoozy.entity.GoalEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.goal.GoalRepository;

import java.util.HashMap;
import java.util.List;

public class MemoryGoalRepository implements GoalRepository {

    private final HashMap<Long, GoalEntity> goals = new HashMap<>();

    private long currentId = 1L;


    @Override
    public void save(GoalEntity goal) {
        goal.setId(currentId);
        goals.put(currentId, goal);
        currentId += 1;
    }

    @Override
    public void update(GoalEntity goal) {
        GoalEntity goalToUpdate = goals.get(goal.getId());
        if (goalToUpdate == null) {
            return;
        }
        if (goal.getName() != null) {
            goalToUpdate.setName(goal.getName());
        }
        if (goal.getGoalAmount() != null) {
            goalToUpdate.setGoalAmount(goal.getGoalAmount());
        }
        if (goal.getCurrentAmount() != null) {
            goalToUpdate.setCurrentAmount(goal.getCurrentAmount());
        }
    }

    @Override
    public boolean delete(Long id) {
        GoalEntity goal = goals.remove(id);
        return goal != null;
    }

    @Override
    public List<GoalEntity> findAll() {
        return goals.values().stream().toList();
    }

    @Override
    public GoalEntity findById(Long id) {
        return goals.values()
                .stream()
                .filter(goal -> goal.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Goal with id=%s not found".formatted(id)));
    }

    @Override
    public List<GoalEntity> findAllByUserProfileId(Long id) {
        return goals.values()
                .stream()
                .filter(goal -> goal.getUserProfile().getId().equals(id))
                .toList();
    }
}
