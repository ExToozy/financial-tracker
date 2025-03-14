package ru.extoozy.repository.goal;

import ru.extoozy.entity.GoalEntity;
import ru.extoozy.repository.CrudRepository;

import java.util.List;

public interface GoalRepository extends CrudRepository<GoalEntity, Long> {
    List<GoalEntity> findAllByUserProfileId(Long id);
}
