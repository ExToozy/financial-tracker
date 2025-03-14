package ru.extoozy.repository.user.impl;

import ru.extoozy.entity.UserEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.user.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MemoryUserRepository implements UserRepository {

    private final HashMap<Long, UserEntity> users = new HashMap<>();

    private long currentId = 1L;

    @Override
    public void save(UserEntity user) {
        user.setId(currentId);
        users.put(currentId, user);
        currentId += 1;
    }

    @Override
    public void update(UserEntity user) {
        UserEntity userToUpdate = users.get(user.getId());
        if (userToUpdate == null) {
            return;
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            userToUpdate.setPassword(user.getPassword());
        }
    }

    @Override
    public boolean delete(Long id) {
        UserEntity user = users.remove(id);
        return user != null;
    }

    @Override
    public List<UserEntity> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public UserEntity findById(Long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("User with id=%s not found".formatted(id)));
    }

    @Override
    public UserEntity getByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User with email=%s not found".formatted(email)));
    }

    @Override
    public void changeBlockStatus(Long id) {
        UserEntity user = findById(id);
        user.setBlocked(!user.isBlocked());
    }
}
