package ru.extoozy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.DeleteUserProfileDto;
import ru.extoozy.dto.profile.GetUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.util.MapperConverter;

import java.util.List;
import java.util.Map;

@Mapper(uses = MapperConverter.class)
public interface UserProfileMapper {

    UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

    UserProfileEntity toEntity(CreateUserProfileDto dto);

    UserProfileEntity toEntity(UpdateUserProfileDto dto);

    UserProfileDto toDto(UserProfileEntity entity);

    @Mapping(target = "firstName", source = "firstname")
    @Mapping(target = "lastName", source = "lastname")
    CreateUserProfileDto toCreateUserProfileDto(Map<String, Object> jsonMap);

    @Mapping(target = "id", source = "user_profile_id")
    @Mapping(target = "firstName", source = "firstname")
    @Mapping(target = "lastName", source = "lastname")
    UpdateUserProfileDto toUpdateUserProfileDto(Map<String, Object> jsonMap);

    @Mapping(target = "userId", source = "user_id")
    GetUserProfileDto toGetUserProfileDto(Map<String, Object> jsonMap);

    @Mapping(target = "userProfileId", source = "user_profile_id")
    DeleteUserProfileDto toDeleteUserProfileDto(Map<String, Object> jsonMap);

    List<UserProfileDto> toDto(List<UserProfileEntity> entities);

}
