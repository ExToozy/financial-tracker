package ru.extoozy.service.profile.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
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
        UserProfileEntity userProfile = UserProfileMapper.INSTANCE.toEntity(dto);
        userProfile.setUser(UserContext.getUser());
        userProfileRepository.save(userProfile);
        UserContext.getUser().setUserProfile(userProfile);
    }

    @Override
    public void update(UpdateUserProfileDto dto) {
        UserProfileEntity userProfile = UserProfileMapper.INSTANCE.toEntity(dto);
        if (usesDoesNotHaveAccessToUserProfile(UserContext.getUser(), userProfileRepository.findById(dto.getId()))) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to user profile with id=%s".formatted(
                            UserContext.getUser().getId(),
                            userProfile.getId()
                    )
            );
        }

        userProfileRepository.update(userProfile);
    }

    @Override
    public UserProfileDto get(Long id) {
        UserProfileEntity userProfile = userProfileRepository.findById(id);
        if (usesDoesNotHaveAccessToUserProfile(UserContext.getUser(), userProfile)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to user profile with id=%s".formatted(
                            UserContext.getUser().getId(),
                            userProfile.getId()
                    )
            );
        }
        return UserProfileMapper.INSTANCE.toDto(userProfile);
    }

    @Override
    public UserProfileDto getByUserId(Long userId) {
        UserProfileEntity userProfile = userProfileRepository.findByUserId(userId);
        if (usesDoesNotHaveAccessToUserProfile(UserContext.getUser(), userProfile)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to user profile with id=%s".formatted(
                            UserContext.getUser().getId(),
                            userProfile.getId()
                    )
            );
        }
        return UserProfileMapper.INSTANCE.toDto(userProfile);
    }


    @Override
    public List<UserProfileDto> getAll() {
        List<UserProfileEntity> userProfiles = userProfileRepository.findAll();
        return UserProfileMapper.INSTANCE.toDto(userProfiles);
    }

    @Override
    public void delete(Long id) {
        UserProfileEntity userProfile = userProfileRepository.findById(id);
        if (usesDoesNotHaveAccessToUserProfile(UserContext.getUser(), userProfile)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to user profile with id=%s".formatted(
                            UserContext.getUser().getId(),
                            userProfile.getId()
                    )
            );
        }
        boolean deleted = userProfileRepository.delete(id);
        if (!deleted) {
            throw new ResourceNotFoundException("User profile with id=%s not found".formatted(id));
        }
    }

    private boolean usesDoesNotHaveAccessToUserProfile(UserEntity user, UserProfileEntity userProfile) {
        return !user.getRole().equals(UserRole.ADMIN) &&
                !user.getUserProfile().getId().equals(userProfile.getId());
    }
}
