package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.service.profile.UserProfileService;

import java.util.List;

@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    public void create(CreateUserProfileDto dto) {
        userProfileService.create(dto);
    }

    public void update(UpdateUserProfileDto dto) {
        userProfileService.update(dto);
    }

    public List<UserProfileDto> getAll() {
        return userProfileService.getAll();
    }

    public UserProfileDto getByUserId(Long userId) {
        return userProfileService.getByUserId(userId);
    }

}
