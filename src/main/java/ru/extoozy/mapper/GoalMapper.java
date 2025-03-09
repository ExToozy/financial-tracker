package ru.extoozy.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.dto.goal.GoalDto;
import ru.extoozy.entity.GoalEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalMapper {

    public static GoalEntity toEntity(CreateGoalDto dto) {
        return GoalEntity.builder()
                .name(dto.getName())
                .goalAmount(dto.getGoalAmount())
                .build();
    }

    public static GoalDto toDto(GoalEntity entity) {
        return GoalDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .goalAmount(entity.getGoalAmount())
                .currentAmount(entity.getCurrentAmount())
                .build();
    }

    public static List<GoalDto> toDto(List<GoalEntity> entities) {
        return entities.stream()
                .map(GoalMapper::toDto)
                .toList();
    }

}
