package ru.extoozy.service.profile.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.UserProfileMapper;
import ru.extoozy.repository.profile.UserProfileRepository;
import ru.extoozy.service.profile.UserProfileService;

import java.util.List;

@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public void create(CreateUserProfileDto dto) {
        UserProfileEntity userProfile = UserProfileMapper.toEntity(dto);
        UserContext.setUser(new UserEntity());
        userProfile.setUser(UserContext.getUser());
        UserContext.getUser().setUserProfile(userProfile);
        userProfileRepository.save(userProfile);
    }

    @Override
    public void update(UpdateUserProfileDto dto) {
        UserProfileEntity userProfile = UserProfileMapper.toEntity(dto);
        userProfileRepository.update(userProfile);
    }

    @Override
    public UserProfileDto get(Long id) {
        UserProfileEntity userProfile = userProfileRepository.findById(id);
        return UserProfileMapper.toDto(userProfile);
    }

    @Override
    public UserProfileDto getByUserId(Long userId) {
        UserProfileEntity userProfile = userProfileRepository.findByUserId(userId);
        return UserProfileMapper.toDto(userProfile);
    }


    @Override
    public List<UserProfileDto> getAll() {
        List<UserProfileEntity> userProfiles = userProfileRepository.findAll();
        return UserProfileMapper.toDto(userProfiles);
    }

    @Override
    public void delete(Long id) {
        boolean deleted = userProfileRepository.delete(id);
        if (!deleted) {
            throw new ResourceNotFoundException("User profile with id=%s not found".formatted(id));
        }
    }
}
