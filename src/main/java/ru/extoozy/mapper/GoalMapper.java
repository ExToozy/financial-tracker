package ru.extoozy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.dto.goal.DeleteGoalDto;
import ru.extoozy.dto.goal.GetGoalsDto;
import ru.extoozy.dto.goal.GoalDto;
import ru.extoozy.dto.goal.ReplenishGoalDto;
import ru.extoozy.entity.GoalEntity;
import ru.extoozy.util.MapperConverter;

import java.util.List;
import java.util.Map;

@Mapper(uses = MapperConverter.class)
public interface GoalMapper {

    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    GoalEntity toEntity(CreateGoalDto dto);

    GoalDto toDto(GoalEntity entity);

    List<GoalDto> toDto(List<GoalEntity> entities);

    @Mapping(target = "goalAmount", source = "goal_amount")
    CreateGoalDto toCreateGoalDto(Map<String, Object> jsonMap);

    @Mapping(target = "goalId", source = "goal_id")
    ReplenishGoalDto toReplenishGoalDto(Map<String, Object> jsonMap);

    @Mapping(target = "goalId", source = "goal_id")
    DeleteGoalDto toDeleteGoalDto(Map<String, Object> jsonMap);

    @Mapping(target = "userProfileId", source = "user_profile_id")
    GetGoalsDto toGetGoalsDto(Map<String, Object> jsonMap);

}
