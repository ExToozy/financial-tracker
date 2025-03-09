package ru.extoozy.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.dto.user.UserDto;
import ru.extoozy.entity.UserEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserEntity toEntity(AuthUserDto dto) {
        return UserEntity.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }

    public static UserEntity toEntity(UpdateUserDto dto) {
        return UserEntity.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }

    public static UserDto toDto(UserEntity dto) {
        return UserDto.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .role(dto.getRole())
                .blocked(dto.isBlocked())
                .build();
    }

    public static List<UserDto> toDto(List<UserEntity> entities) {
        return entities.stream()
                .map(UserMapper::toDto)
                .toList();
    }

}
