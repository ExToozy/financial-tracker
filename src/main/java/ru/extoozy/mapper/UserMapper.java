package ru.extoozy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.dto.user.DeleteUserDto;
import ru.extoozy.dto.user.GetUserDto;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.dto.user.UserDto;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.util.MapperConverter;

import java.util.List;
import java.util.Map;

@Mapper(uses = MapperConverter.class)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toEntity(AuthUserDto dto);

    UserEntity toEntity(UpdateUserDto dto);

    AuthUserDto toAuthDto(Map<String, Object> jsonMap);

    @Mapping(target = "id", source = "user_id")
    UpdateUserDto toUpdateDto(Map<String, Object> jsonMap);

    @Mapping(target = "userId", source = "user_id")
    GetUserDto toGetUserDto(Map<String, Object> jsonMap);

    @Mapping(target = "userId", source = "user_id")
    DeleteUserDto toDeleteUserDto(Map<String, Object> jsonMap);

    UserDto toDto(UserEntity dto);

    List<UserDto> toDto(List<UserEntity> entities);

}
