package ru.extoozy.service.user.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.dto.user.UserDto;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.UserMapper;
import ru.extoozy.repository.user.UserRepository;
import ru.extoozy.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void create(AuthUserDto dto) {
        UserEntity user = UserMapper.toEntity(dto);
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    @Override
    public void update(UpdateUserDto dto) {
        UserEntity entity = UserMapper.toEntity(dto);
        userRepository.update(entity);
    }

    @Override
    public UserDto get(Long id) {
        UserEntity user = userRepository.findById(id);
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserEntity> users = userRepository.findAll();
        return UserMapper.toDto(users);
    }

    @Override
    public void delete(Long id) {
        boolean deleted = userRepository.delete(id);
        if (!deleted) {
            throw new ResourceNotFoundException("User with id=%s not found".formatted(id));
        }
    }

    @Override
    public boolean containsByEmail(String email) {
        try {
            userRepository.getByEmail(email);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    @Override
    public void changeBlockStatus(Long id) {
        userRepository.changeBlockStatus(id);
    }
}
