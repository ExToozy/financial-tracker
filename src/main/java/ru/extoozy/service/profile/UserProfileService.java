package ru.extoozy.service.profile;

import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;

import java.util.List;

public interface UserProfileService {

    void create(CreateUserProfileDto dto);

    void update(UpdateUserProfileDto dto);

    UserProfileDto get(Long id);

    UserProfileDto getByUserId(Long userId);

    List<UserProfileDto> getAll();

    void delete(Long id);
}
