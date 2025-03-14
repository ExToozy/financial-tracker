package ru.extoozy.repository.profile.impl;

import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.profile.UserProfileRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MemoryUserProfileRepository implements UserProfileRepository {

    private final HashMap<Long, UserProfileEntity> userProfiles = new HashMap<>();

    private long currentId = 1L;

    @Override
    public void save(UserProfileEntity userProfile) {
        userProfile.setId(currentId);
        userProfiles.put(currentId, userProfile);
        currentId += 1;
    }

    @Override
    public void update(UserProfileEntity userProfile) {
        UserProfileEntity userProfileToUpdate = userProfiles.get(userProfile.getId());
        if (userProfileToUpdate == null) {
            return;
        }
        if (userProfile.getFirstName() != null) {
            userProfileToUpdate.setFirstName(userProfile.getFirstName());
        }
        if (userProfile.getLastName() != null) {
            userProfileToUpdate.setLastName(userProfile.getLastName());
        }
    }

    @Override
    public boolean delete(Long id) {
        UserProfileEntity userProfile = userProfiles.remove(id);
        return userProfile != null;
    }

    @Override
    public List<UserProfileEntity> findAll() {
        return userProfiles.values().stream().toList();
    }

    @Override
    public UserProfileEntity findById(Long id) {
        return Optional.ofNullable(userProfiles.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("User profile with id=%s not found".formatted(id)));
    }

    @Override
    public UserProfileEntity findByUserId(Long id) {
        return userProfiles.values()
                .stream()
                .filter(userProfile -> userProfile.getUser().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User profile with id=%s not found".formatted(id)));
    }
}
