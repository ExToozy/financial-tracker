package ru.extoozy.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.entity.UserProfileEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProfileMapper {

    public static UserProfileEntity toEntity(CreateUserProfileDto dto) {
        return UserProfileEntity.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();
    }

    public static UserProfileEntity toEntity(UpdateUserProfileDto dto) {
        return UserProfileEntity.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();
    }

    public static UserProfileDto toDto(UserProfileEntity entity) {
        return UserProfileDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();
    }

    public static List<UserProfileDto> toDto(List<UserProfileEntity> entities) {
        return entities.stream()
                .map(UserProfileMapper::toDto)
                .toList();
    }

}
